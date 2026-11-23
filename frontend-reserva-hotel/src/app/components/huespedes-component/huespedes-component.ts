import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { IconComponent } from '../../layout/icon/icon';
import { HuespedService } from '../../services/huesped-service';
import { Huesped, HuespedRequest } from '../../models/huesped';

@Component({
  imports: [ReactiveFormsModule, PageHeaderComponent, IconComponent],
  selector: 'app-huespedes-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './huespedes-component.css',
  templateUrl: './huespedes-component.html',
})
export class HuespedesComponent implements OnInit {
  private fb = inject(FormBuilder);
  private huespedService = inject(HuespedService);

  huespedes = signal<Huesped[]>([]);
  cargando = signal<boolean>(true);
  errorCarga = signal<string | null>(null);
  filtro = signal<string>('');
  mensajeExito = signal<string | null>(null);

  // Estado del modal de creación / edición
  modalAbierto = signal<boolean>(false);
  editandoId = signal<number | null>(null);
  guardando = signal<boolean>(false);
  errorFormulario = signal<string | null>(null);

  // Estado del modal de confirmación para eliminar
  huespedAEliminar = signal<Huesped | null>(null);
  eliminando = signal<boolean>(false);
  errorEliminar = signal<string | null>(null);

  readonly filasEsqueleto = [1, 2, 3, 4, 5];

  huespedesFiltrados = computed(() => {
    const q = this.filtro().trim().toLowerCase();
    const lista = this.huespedes();
    if (!q) return lista;
    return lista.filter(h =>
      h.cedula.toLowerCase().includes(q) ||
      h.nombres.toLowerCase().includes(q) ||
      h.apellidos.toLowerCase().includes(q) ||
      h.email.toLowerCase().includes(q) ||
      (h.telefono ?? '').toLowerCase().includes(q)
    );
  });

  frmHuesped = this.fb.nonNullable.group({
    cedula: ['', [Validators.required, Validators.maxLength(13)]],
    nombres: ['', [Validators.required, Validators.maxLength(60)]],
    apellidos: ['', [Validators.required, Validators.maxLength(60)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
    telefono: ['', [Validators.maxLength(20)]],
  });

  ngOnInit(): void {
    this.cargarHuespedes();
  }

  cargarHuespedes(): void {
    this.cargando.set(true);
    this.errorCarga.set(null);

    this.huespedService.listar().subscribe({
      next: lista => {
        this.huespedes.set(lista);
        this.cargando.set(false);
      },
      error: (err: Error) => {
        this.errorCarga.set(err.message);
        this.cargando.set(false);
      },
    });
  }

  abrirNuevo(): void {
    this.editandoId.set(null);
    this.errorFormulario.set(null);
    this.frmHuesped.reset({
      cedula: '',
      nombres: '',
      apellidos: '',
      email: '',
      telefono: '',
    });
    this.modalAbierto.set(true);
  }

  abrirEditar(huesped: Huesped): void {
    this.editandoId.set(huesped.id);
    this.errorFormulario.set(null);
    this.frmHuesped.reset({
      cedula: huesped.cedula,
      nombres: huesped.nombres,
      apellidos: huesped.apellidos,
      email: huesped.email,
      telefono: huesped.telefono ?? '',
    });
    this.modalAbierto.set(true);
  }

  cerrarModal(): void {
    if (this.guardando()) return;
    this.modalAbierto.set(false);
    this.editandoId.set(null);
    this.errorFormulario.set(null);
  }

  guardar(): void {
    if (this.frmHuesped.invalid) {
      this.frmHuesped.markAllAsTouched();
      return;
    }

    this.guardando.set(true);
    this.errorFormulario.set(null);

    const raw = this.frmHuesped.getRawValue();
    const payload: HuespedRequest = {
      cedula: raw.cedula.trim(),
      nombres: raw.nombres.trim(),
      apellidos: raw.apellidos.trim(),
      email: raw.email.trim(),
      telefono: raw.telefono.trim() ? raw.telefono.trim() : null,
    };

    const id = this.editandoId();
    const peticion$ = id === null
      ? this.huespedService.crear(payload)
      : this.huespedService.actualizar(id, payload);

    peticion$.subscribe({
      next: res => {
        if (id === null) {
          this.huespedes.update(lista => [...lista, res.result]);
        } else {
          this.huespedes.update(lista =>
            lista.map(item => (item.id === id ? res.result : item))
          );
        }
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.editandoId.set(null);
        this.mostrarExito(res.message);
      },
      error: (err: Error) => {
        this.errorFormulario.set(err.message);
        this.guardando.set(false);
      },
    });
  }

  confirmarEliminar(huesped: Huesped): void {
    this.errorEliminar.set(null);
    this.huespedAEliminar.set(huesped);
  }

  cancelarEliminar(): void {
    if (this.eliminando()) return;
    this.huespedAEliminar.set(null);
    this.errorEliminar.set(null);
  }

  ejecutarEliminar(): void {
    const objetivo = this.huespedAEliminar();
    if (!objetivo) return;

    this.eliminando.set(true);
    this.errorEliminar.set(null);

    this.huespedService.eliminar(objetivo.id).subscribe({
      next: res => {
        this.huespedes.update(lista => lista.filter(h => h.id !== objetivo.id));
        this.eliminando.set(false);
        this.huespedAEliminar.set(null);
        this.mostrarExito(res.message);
      },
      error: (err: Error) => {
        this.errorEliminar.set(err.message);
        this.eliminando.set(false);
      },
    });
  }

  private mostrarExito(mensaje: string): void {
    this.mensajeExito.set(mensaje);
    setTimeout(() => {
      if (this.mensajeExito() === mensaje) {
        this.mensajeExito.set(null);
      }
    }, 4000);
  }
}