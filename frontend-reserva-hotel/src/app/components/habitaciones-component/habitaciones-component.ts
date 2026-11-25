import { CurrencyPipe } from '@angular/common';
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
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { IconComponent } from '../../layout/icon/icon';
import { EstadoHabitacion, HabitacionRequest, HabitacionResponse } from '../../models/habitacion';
import { TipoHabitacionResponse } from '../../models/tipo-habitacion';
import { HabitacionService } from '../../services/habitacion-service';
import { TipoHabitacionService } from '../../services/tipo-habitacion-service';

const textoNoVacio: ValidatorFn = (control) =>
  typeof control.value === 'string' && !control.value.trim() ? { blanco: true } : null;

const numeroEntero: ValidatorFn = (control) =>
  control.value == null || Number.isInteger(control.value) ? null : { entero: true };

const dosDecimales: ValidatorFn = (control) =>
  control.value == null || /^\d+(?:\.\d{1,2})?$/.test(String(control.value))
    ? null
    : { decimales: true };

const estadoValido: ValidatorFn = (control) =>
  ['DISPONIBLE', 'MANTENIMIENTO'].includes(control.value) ? null : { estado: true };

@Component({
  imports: [CurrencyPipe, PageHeaderComponent, IconComponent, ReactiveFormsModule, RouterLink],
  selector: 'app-habitaciones-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './habitaciones-component.css',
  templateUrl: './habitaciones-component.html',
})
export class HabitacionesComponent implements OnInit {
  private readonly service = inject(HabitacionService);
  private readonly tipoService = inject(TipoHabitacionService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly editor = viewChild.required<ElementRef<HTMLDialogElement>>('editor');
  private readonly confirmacion = viewChild.required<ElementRef<HTMLDialogElement>>('confirmacion');

  readonly habitaciones = signal<HabitacionResponse[]>([]);
  readonly tipos = signal<TipoHabitacionResponse[]>([]);
  readonly busqueda = signal('');
  readonly filtroEstado = signal<'TODAS' | EstadoHabitacion>('TODAS');
  readonly cargando = signal(false);
  readonly cargandoTipos = signal(false);
  readonly cargandoEdicion = signal<number | null>(null);
  readonly guardando = signal(false);
  readonly eliminando = signal(false);
  readonly idEditando = signal<number | null>(null);
  readonly habitacionAEliminar = signal<HabitacionResponse | null>(null);
  readonly errorListado = signal<string | null>(null);
  readonly errorTipos = signal<string | null>(null);
  readonly errorFormulario = signal<string | null>(null);
  readonly errorEliminacion = signal<string | null>(null);
  readonly mensajeExito = signal<string | null>(null);

  readonly ocupado = computed(
    () =>
      this.cargando() || this.cargandoEdicion() !== null || this.guardando() || this.eliminando(),
  );
  readonly tiposDisponibles = computed(
    () => !this.cargandoTipos() && !this.errorTipos() && this.tipos().length > 0,
  );
  readonly habitacionesFiltradas = computed(() => {
    const texto = this.busqueda().trim().toLocaleLowerCase('es');
    const estado = this.filtroEstado();
    return this.habitaciones()
      .filter(
        (habitacion) =>
          (estado === 'TODAS' || habitacion.estado === estado) &&
          `${habitacion.numero} ${habitacion.tipoHabitacionNombre}`
            .toLocaleLowerCase('es')
            .includes(texto),
      )
      .sort((a, b) => a.numero.localeCompare(b.numero, 'es', { numeric: true }));
  });

  readonly formulario = this.fb.group({
    numero: ['', [Validators.required, textoNoVacio, Validators.maxLength(20)]],
    piso: this.fb.control<number | null>(0, [
      Validators.required,
      Validators.min(0),
      Validators.max(2147483647),
      numeroEntero,
    ]),
    precioNoche: this.fb.control<number | null>(null, [
      Validators.required,
      Validators.min(0.01),
      Validators.max(99999999.99),
      dosDecimales,
    ]),
    estado: ['DISPONIBLE' as EstadoHabitacion, [Validators.required, estadoValido]],
    tipoHabitacionId: this.fb.control<number | null>(null, [
      Validators.required,
      Validators.min(1),
      numeroEntero,
    ]),
  });

  ngOnInit(): void {
    this.cargarHabitaciones();
    this.cargarTipos();
  }

  cargarHabitaciones(): void {
    if (this.ocupado()) return;
    this.cargando.set(true);
    this.errorListado.set(null);
    this.service
      .listar()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.cargando.set(false)),
      )
      .subscribe({
        next: (habitaciones) => this.habitaciones.set(habitaciones),
        error: (error) =>
          this.errorListado.set(
            this.mensajeError(error, 'No se pudieron cargar las habitaciones.'),
          ),
      });
  }

  cargarTipos(): void {
    if (this.cargandoTipos() || this.guardando()) return;
    this.cargandoTipos.set(true);
    this.errorTipos.set(null);
    this.tipoService
      .listar()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.cargandoTipos.set(false)),
      )
      .subscribe({
        next: (tipos) => {
          this.tipos.set(
            [...tipos].sort((a, b) =>
              a.nombre.localeCompare(b.nombre, 'es', { sensitivity: 'base' }),
            ),
          );
          this.formulario.controls.tipoHabitacionId.updateValueAndValidity();
        },
        error: (error) =>
          this.errorTipos.set(
            this.mensajeError(error, 'No se pudieron cargar los tipos de habitación.'),
          ),
      });
  }

  nuevo(): void {
    if (this.ocupado()) return;
    this.idEditando.set(null);
    this.formulario.reset({
      numero: '',
      piso: 0,
      precioNoche: null,
      estado: 'DISPONIBLE',
      tipoHabitacionId: null,
    });
    this.errorFormulario.set(null);
    this.mensajeExito.set(null);
    this.cargarTipos();
    this.editor().nativeElement.showModal();
  }

  editar(habitacion: HabitacionResponse): void {
    if (this.ocupado()) return;
    this.cargandoEdicion.set(habitacion.id);
    this.errorListado.set(null);
    this.mensajeExito.set(null);
    this.cargarTipos();
    this.service
      .buscar(habitacion.id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.cargandoEdicion.set(null)),
      )
      .subscribe({
        next: (actual) => {
          this.idEditando.set(actual.id);
          this.formulario.reset({
            numero: actual.numero,
            piso: actual.piso,
            precioNoche: actual.precioNoche,
            estado: actual.estado,
            tipoHabitacionId: actual.tipoHabitacionId,
          });
          this.errorFormulario.set(null);
          this.editor().nativeElement.showModal();
        },
        error: (error) =>
          this.errorListado.set(this.mensajeError(error, 'No se pudo consultar la habitación.')),
      });
  }

  guardar(): void {
    if (this.guardando() || !this.tiposDisponibles()) return;
    this.formulario.patchValue({ numero: this.formulario.controls.numero.value.trim() });
    this.formulario.markAllAsTouched();
    const { numero, piso, precioNoche, estado, tipoHabitacionId } = this.formulario.getRawValue();
    if (!this.tipoDisponible(tipoHabitacionId)) {
      this.formulario.controls.tipoHabitacionId.setErrors({ inexistente: true });
    }
    if (
      this.formulario.invalid ||
      piso === null ||
      precioNoche === null ||
      tipoHabitacionId === null
    ) {
      return;
    }
    const datos: HabitacionRequest = { numero, piso, precioNoche, estado, tipoHabitacionId };
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
        next: (habitacion) => {
          this.habitaciones.update((actuales) =>
            id === null
              ? [...actuales, habitacion]
              : actuales.map((actual) => (actual.id === id ? habitacion : actual)),
          );
          this.finalizarFormulario();
          this.mensajeExito.set(
            id === null
              ? 'Habitación creada correctamente.'
              : 'Habitación actualizada correctamente.',
          );
        },
        error: (error) =>
          this.errorFormulario.set(this.mensajeError(error, 'No se pudo guardar la habitación.')),
      });
  }

  cerrarFormulario(): void {
    if (!this.guardando()) this.finalizarFormulario();
  }

  solicitarEliminar(habitacion: HabitacionResponse): void {
    if (this.ocupado()) return;
    this.habitacionAEliminar.set(habitacion);
    this.errorEliminacion.set(null);
    this.mensajeExito.set(null);
    this.confirmacion().nativeElement.showModal();
  }

  confirmarEliminar(): void {
    const habitacion = this.habitacionAEliminar();
    if (!habitacion || this.eliminando()) return;
    this.eliminando.set(true);
    this.errorEliminacion.set(null);
    this.service
      .eliminar(habitacion.id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.eliminando.set(false)),
      )
      .subscribe({
        next: () => {
          this.habitaciones.update((actuales) =>
            actuales.filter((actual) => actual.id !== habitacion.id),
          );
          this.finalizarEliminacion();
          this.mensajeExito.set('Habitación eliminada correctamente.');
        },
        error: (error) =>
          this.errorEliminacion.set(this.mensajeError(error, 'No se pudo eliminar la habitación.')),
      });
  }

  cancelarEliminacion(): void {
    if (!this.eliminando()) this.finalizarEliminacion();
  }

  limpiarFiltros(): void {
    this.busqueda.set('');
    this.filtroEstado.set('TODAS');
  }

  tipoDisponible(id: number | null): boolean {
    return this.tipos().some((tipo) => tipo.id === id);
  }

  invalido(campo: keyof HabitacionRequest): boolean {
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
    this.habitacionAEliminar.set(null);
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
