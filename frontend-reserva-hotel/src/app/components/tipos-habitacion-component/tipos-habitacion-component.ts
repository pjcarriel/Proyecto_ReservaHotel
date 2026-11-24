import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  ElementRef,
  inject,
  OnInit,
  signal,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { IconComponent } from '../../layout/icon/icon';
import { TipoHabitacionRequest, TipoHabitacionResponse } from '../../models/tipo-habitacion';
import { TipoHabitacionService } from '../../services/tipo-habitacion-service';

const textoNoVacio: ValidatorFn = (control) =>
  typeof control.value === 'string' && !control.value.trim() ? { blanco: true } : null;

const numeroEntero: ValidatorFn = (control) =>
  control.value == null || Number.isInteger(control.value) ? null : { entero: true };

@Component({
  imports: [PageHeaderComponent, IconComponent, ReactiveFormsModule],
  selector: 'app-tipos-habitacion-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './tipos-habitacion-component.css',
  templateUrl: './tipos-habitacion-component.html',
})
export class TiposHabitacionComponent implements OnInit {
  private readonly service = inject(TipoHabitacionService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly editor = viewChild.required<ElementRef<HTMLDialogElement>>('editor');
  private readonly confirmacion = viewChild.required<ElementRef<HTMLDialogElement>>('confirmacion');

  readonly tipos = signal<TipoHabitacionResponse[]>([]);
  readonly busqueda = signal('');
  readonly cargando = signal(false);
  readonly cargandoEdicion = signal<number | null>(null);
  readonly guardando = signal(false);
  readonly eliminando = signal(false);
  readonly idEditando = signal<number | null>(null);
  readonly tipoAEliminar = signal<TipoHabitacionResponse | null>(null);
  readonly errorListado = signal<string | null>(null);
  readonly errorFormulario = signal<string | null>(null);
  readonly errorEliminacion = signal<string | null>(null);
  readonly mensajeExito = signal<string | null>(null);

  readonly ocupado = computed(
    () =>
      this.cargando() || this.cargandoEdicion() !== null || this.guardando() || this.eliminando(),
  );

  readonly tiposFiltrados = computed(() => {
    const texto = this.busqueda().trim().toLocaleLowerCase('es');
    return this.tipos()
      .filter((tipo) =>
        `${tipo.nombre} ${tipo.descripcion}`.toLocaleLowerCase('es').includes(texto),
      )
      .sort((a, b) => a.nombre.localeCompare(b.nombre, 'es', { sensitivity: 'base' }));
  });

  readonly formulario = this.fb.group({
    nombre: ['', [Validators.required, textoNoVacio, Validators.maxLength(60)]],
    descripcion: [
      '',
      [Validators.required, textoNoVacio, Validators.minLength(15), Validators.maxLength(200)],
    ],
    capacidad: [
      1,
      [Validators.required, Validators.min(1), Validators.max(2147483647), numeroEntero],
    ],
  });

  ngOnInit(): void {
    this.cargarTipos();
  }

  cargarTipos(): void {
    if (this.cargando()) return;
    this.cargando.set(true);
    this.errorListado.set(null);
    this.service
      .listar()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.cargando.set(false)),
      )
      .subscribe({
        next: (tipos) => this.tipos.set(tipos),
        error: (error) =>
          this.errorListado.set(
            this.mensajeError(error, 'No se pudieron cargar los tipos de habitación.'),
          ),
      });
  }

  nuevo(): void {
    if (this.ocupado()) return;
    this.idEditando.set(null);
    this.formulario.reset({ nombre: '', descripcion: '', capacidad: 1 });
    this.errorFormulario.set(null);
    this.mensajeExito.set(null);
    this.editor().nativeElement.showModal();
  }

  editar(tipo: TipoHabitacionResponse): void {
    if (this.ocupado()) return;
    this.cargandoEdicion.set(tipo.id);
    this.errorListado.set(null);
    this.mensajeExito.set(null);
    this.service
      .buscar(tipo.id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.cargandoEdicion.set(null)),
      )
      .subscribe({
        next: (actual) => {
          this.idEditando.set(actual.id);
          this.formulario.reset({
            nombre: actual.nombre,
            descripcion: actual.descripcion,
            capacidad: actual.capacidad,
          });
          this.errorFormulario.set(null);
          this.editor().nativeElement.showModal();
        },
        error: (error) =>
          this.errorListado.set(
            this.mensajeError(error, 'No se pudo consultar el tipo de habitación.'),
          ),
      });
  }

  guardar(): void {
    if (this.guardando()) return;
    const valores = this.formulario.getRawValue();
    this.formulario.patchValue({
      nombre: valores.nombre.trim(),
      descripcion: valores.descripcion.trim(),
    });
    this.formulario.markAllAsTouched();
    if (this.formulario.invalid) return;

    const datos: TipoHabitacionRequest = this.formulario.getRawValue();
    const id = this.idEditando();
    const operacion = id === null ? this.service.crear(datos) : this.service.actualizar(id, datos);
    this.guardando.set(true);
    this.errorFormulario.set(null);
    operacion
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.guardando.set(false)),
      )
      .subscribe({
        next: (tipo) => {
          this.tipos.update((actuales) =>
            id === null
              ? [...actuales, tipo]
              : actuales.map((actual) => (actual.id === id ? tipo : actual)),
          );
          this.finalizarFormulario();
          this.mensajeExito.set(
            id === null
              ? 'Tipo de habitación creado correctamente.'
              : 'Tipo de habitación actualizado correctamente.',
          );
        },
        error: (error) =>
          this.errorFormulario.set(
            this.mensajeError(error, 'No se pudo guardar el tipo de habitación.'),
          ),
      });
  }

  cerrarFormulario(): void {
    if (!this.guardando()) this.finalizarFormulario();
  }

  solicitarEliminar(tipo: TipoHabitacionResponse): void {
    if (this.ocupado()) return;
    this.tipoAEliminar.set(tipo);
    this.errorEliminacion.set(null);
    this.mensajeExito.set(null);
    this.confirmacion().nativeElement.showModal();
  }

  confirmarEliminar(): void {
    const tipo = this.tipoAEliminar();
    if (!tipo || this.eliminando()) return;
    this.eliminando.set(true);
    this.errorEliminacion.set(null);
    this.service
      .eliminar(tipo.id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.eliminando.set(false)),
      )
      .subscribe({
        next: () => {
          this.tipos.update((actuales) => actuales.filter((actual) => actual.id !== tipo.id));
          this.finalizarEliminacion();
          this.mensajeExito.set('Tipo de habitación eliminado correctamente.');
        },
        error: (error) =>
          this.errorEliminacion.set(
            this.mensajeError(error, 'No se pudo eliminar el tipo de habitación.'),
          ),
      });
  }

  cancelarEliminacion(): void {
    if (!this.eliminando()) this.finalizarEliminacion();
  }

  invalido(campo: keyof TipoHabitacionRequest): boolean {
    const control = this.formulario.controls[campo];
    return control.invalid && control.touched;
  }

  private finalizarFormulario(): void {
    this.editor().nativeElement.close();
    this.idEditando.set(null);
    this.errorFormulario.set(null);
  }

  private finalizarEliminacion(): void {
    this.confirmacion().nativeElement.close();
    this.tipoAEliminar.set(null);
    this.errorEliminacion.set(null);
  }

  private mensajeError(error: unknown, alternativa: string): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) return 'No se pudo conectar con el servidor. Intenta nuevamente.';
      return typeof error.error?.message === 'string' ? error.error.message : alternativa;
    }
    return error instanceof Error && error.message ? error.message : alternativa;
  }
}
