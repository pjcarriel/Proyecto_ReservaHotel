import { ChangeDetectionStrategy, Component, computed, DestroyRef, ElementRef, inject, OnInit, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { finalize } from 'rxjs';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { IconComponent } from '../../layout/icon/icon';
import { Usuario, UsuarioRequest } from '../../models/usuario';
import { Rol } from '../../models/rol';
import { UsuarioService } from '../../services/usuario-service';
import { LoginService } from '../../services/login-service';

@Component({
  imports: [PageHeaderComponent, IconComponent, ReactiveFormsModule, DatePipe],
  selector: 'app-usuarios-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './usuarios-component.css',
  templateUrl: './usuarios-component.html',
})
export class UsuariosComponent implements OnInit {
  private readonly service = inject(UsuarioService);
  private readonly loginService = inject(LoginService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly editor = viewChild.required<ElementRef<HTMLDialogElement>>('editor');

  readonly roles = [Rol.ADMIN, Rol.RECEPCIONISTA];

  readonly usuarios = signal<Usuario[]>([]);
  readonly busqueda = signal('');
  readonly cargando = signal(false);
  readonly guardando = signal(false);
  readonly cambiandoEstado = signal<number | null>(null);
  readonly idEditando = signal<number | null>(null);
  readonly verPassword = signal(false);
  readonly errorListado = signal<string | null>(null);
  readonly errorFormulario = signal<string | null>(null);
  readonly mensajeExito = signal<string | null>(null);

  // Usuario que inició sesión: no se puede desactivar a sí mismo
  readonly usernameActual = computed(() => this.loginService.usuarioSession()?.username ?? null);

  readonly ocupado = computed(() => this.cargando() || this.guardando() || this.cambiandoEstado() !== null);

  readonly usuariosFiltrados = computed(() => {
    const texto = this.busqueda().trim().toLocaleLowerCase('es');
    return this.usuarios()
      .filter(u => u.username.toLocaleLowerCase('es').includes(texto))
      .sort((a, b) => a.username.localeCompare(b.username, 'es', { sensitivity: 'base' }));
  });

  readonly resumen = computed(() => {
    const lista = this.usuarios();
    return {
      total: lista.length,
      activos: lista.filter(u => u.activo).length,
      admins: lista.filter(u => u.rol === Rol.ADMIN).length,
    };
  });

  readonly formulario = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(30), Validators.pattern(/^\S+$/)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(100)]],
    rol: [Rol.RECEPCIONISTA, Validators.required],
  });

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    if (this.cargando()) return;
    this.cargando.set(true);
    this.errorListado.set(null);
    this.service.listar()
      .pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.cargando.set(false)))
      .subscribe({
        next: usuarios => this.usuarios.set(usuarios),
        error: (er: Error) => this.errorListado.set(er.message || 'No se pudieron cargar los usuarios.'),
      });
  }

  nuevo(): void {
    if (this.ocupado()) return;
    this.idEditando.set(null);
    this.formulario.reset({ username: '', password: '', rol: Rol.RECEPCIONISTA });
    this.abrirFormulario();
  }

  editar(usuario: Usuario): void {
    if (this.ocupado()) return;
    this.idEditando.set(usuario.id);
    // El backend pide el password también al editar (se guarda encriptado otra vez)
    this.formulario.reset({ username: usuario.username, password: '', rol: usuario.rol });
    this.abrirFormulario();
  }

  guardar(): void {
    if (this.guardando()) return;
    this.formulario.patchValue({ username: this.formulario.controls.username.value.trim() });
    this.formulario.markAllAsTouched();
    if (this.formulario.invalid) return;

    const datos: UsuarioRequest = this.formulario.getRawValue();
    const id = this.idEditando();
    const operacion = id === null ? this.service.crear(datos) : this.service.actualizar(id, datos);

    this.guardando.set(true);
    this.errorFormulario.set(null);
    operacion
      .pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.guardando.set(false)))
      .subscribe({
        next: usuario => {
          this.usuarios.update(lista => id === null
            ? [...lista, usuario]
            : lista.map(u => (u.id === id ? usuario : u)));
          this.cerrarFormulario();
          this.mensajeExito.set(id === null
            ? `Usuario «${usuario.username}» creado correctamente.`
            : `Usuario «${usuario.username}» actualizado correctamente.`);
        },
        error: (er: Error) => this.errorFormulario.set(er.message || 'No se pudo guardar el usuario.'),
      });
  }

  cambiarEstado(usuario: Usuario): void {
    if (this.ocupado() || this.esUsuarioActual(usuario)) return;
    const activo = !usuario.activo;
    this.cambiandoEstado.set(usuario.id);
    this.mensajeExito.set(null);
    this.errorListado.set(null);
    this.service.cambiarEstado(usuario.id, activo)
      .pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.cambiandoEstado.set(null)))
      .subscribe({
        next: actualizado => {
          this.usuarios.update(lista => lista.map(u => (u.id === actualizado.id ? actualizado : u)));
          this.mensajeExito.set(`Usuario «${actualizado.username}» ${actualizado.activo ? 'activado' : 'desactivado'}.`);
        },
        error: (er: Error) => this.errorListado.set(er.message || 'No se pudo cambiar el estado del usuario.'),
      });
  }

  cerrarFormulario(): void {
    if (this.guardando()) return;
    this.editor().nativeElement.close();
    this.idEditando.set(null);
    this.errorFormulario.set(null);
  }

  esUsuarioActual(usuario: Usuario): boolean {
    return usuario.username === this.usernameActual();
  }

  rolTexto(rol: Rol): string {
    return rol === Rol.ADMIN ? 'Administrador' : 'Recepcionista';
  }

  invalido(campo: keyof UsuarioRequest): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && control.touched;
  }

  private abrirFormulario(): void {
    this.verPassword.set(false);
    this.errorFormulario.set(null);
    this.mensajeExito.set(null);
    this.editor().nativeElement.showModal();
  }
}
