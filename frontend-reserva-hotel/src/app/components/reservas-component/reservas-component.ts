import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { IconComponent } from '../../layout/icon/icon';
import { ReservaService } from '../../services/reserva-service';
import { HuespedService } from '../../services/huesped-service';
import { HabitacionService } from '../../services/habitacion-service';
import { EstadoReserva, Reserva } from '../../models/reserva';
import { Huesped } from '../../models/huesped';
import { Habitacion } from '../../models/habitacion';

type FiltroEstado = 'TODAS' | EstadoReserva;

@Component({
  imports: [CurrencyPipe, ReactiveFormsModule, PageHeaderComponent, IconComponent],
  selector: 'app-reservas-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './reservas-component.css',
  templateUrl: './reservas-component.html',
})
export class ReservasComponent implements OnInit {
  private fb = inject(FormBuilder);
  private reservaService = inject(ReservaService);
  private huespedService = inject(HuespedService);
  private habitacionService = inject(HabitacionService);

  readonly IVA_PORCENTAJE = 0.15;
  readonly fechaMinimaHoy = ReservasComponent.hoyISO();

  reservas = signal<Reserva[]>([]);
  cargando = signal<boolean>(true);
  errorCarga = signal<string | null>(null);
  filtroTexto = signal<string>('');
  filtroEstado = signal<FiltroEstado>('TODAS');
  mensajeExito = signal<string | null>(null);

  // Modal de detalle
  reservaDetalle = signal<Reserva | null>(null);

  // Modal de cancelación
  reservaACancelar = signal<Reserva | null>(null);
  cancelando = signal<boolean>(false);
  errorCancelar = signal<string | null>(null);

  // Modal de nueva reserva
  modalCrearAbierto = signal<boolean>(false);
  cargandoCatalogos = signal<boolean>(false);
  errorCatalogos = signal<string | null>(null);
  huespedes = signal<Huesped[]>([]);
  habitaciones = signal<Habitacion[]>([]);
  habitacionesSeleccionadas = signal<number[]>([]);
  guardando = signal<boolean>(false);
  errorGuardar = signal<string | null>(null);

  // Señales espejo para el cálculo reactivo del resumen
  fechaEntradaValor = signal<string>('');
  fechaSalidaValor = signal<string>('');

  formReserva = this.fb.nonNullable.group({
    huespedId: [0, [Validators.required, Validators.min(1)]],
    fechaEntrada: ['', [Validators.required]],
    fechaSalida: ['', [Validators.required]],
  });

  readonly filasEsqueleto = [1, 2, 3, 4, 5];

  reservasFiltradas = computed(() => {
    const q = this.filtroTexto().trim().toLowerCase();
    const estado = this.filtroEstado();
    return this.reservas().filter(r => {
      if (estado !== 'TODAS' && r.estado !== estado) return false;
      if (!q) return true;
      return (
        r.numero.toLowerCase().includes(q) ||
        (r.huespedNombre ?? '').toLowerCase().includes(q) ||
        (r.huespedCedula ?? '').toLowerCase().includes(q)
      );
    });
  });

  habitacionesDisponibles = computed(() =>
    this.habitaciones().filter(h => h.estado === 'DISPONIBLE')
  );

  nochesCalculadas = computed(() => {
    const entrada = this.fechaEntradaValor();
    const salida = this.fechaSalidaValor();
    if (!entrada || !salida) return 0;
    const ini = new Date(`${entrada}T00:00:00`).getTime();
    const fin = new Date(`${salida}T00:00:00`).getTime();
    const dias = Math.round((fin - ini) / 86_400_000);
    return dias > 0 ? dias : 0;
  });

  fechasInvalidas = computed(() => {
    const entrada = this.fechaEntradaValor();
    const salida = this.fechaSalidaValor();
    if (!entrada || !salida) return false;
    return this.nochesCalculadas() <= 0;
  });

  detallesResumen = computed(() => {
    const noches = this.nochesCalculadas();
    const ids = new Set(this.habitacionesSeleccionadas());
    return this.habitaciones()
      .filter(h => ids.has(h.id))
      .map(h => ({
        habitacion: h,
        noches,
        subtotal: this.redondear(h.precioNoche * noches),
      }));
  });

  subtotalCalculado = computed(() =>
    this.redondear(this.detallesResumen().reduce((acc, item) => acc + item.subtotal, 0))
  );

  ivaCalculado = computed(() =>
    this.redondear(this.subtotalCalculado() * this.IVA_PORCENTAJE)
  );

  totalCalculado = computed(() =>
    this.redondear(this.subtotalCalculado() + this.ivaCalculado())
  );

  ngOnInit(): void {
    this.cargarReservas();
  }

  cargarReservas(): void {
    this.cargando.set(true);
    this.errorCarga.set(null);

    this.reservaService.listar().subscribe({
      next: lista => {
        this.reservas.set(lista);
        this.cargando.set(false);
      },
      error: (err: Error) => {
        this.errorCarga.set(err.message);
        this.cargando.set(false);
      },
    });
  }

  abrirModalCrear(): void {
    const hoy = ReservasComponent.hoyISO();
    const manana = ReservasComponent.sumarDiasISO(hoy, 1);

    this.formReserva.reset({
      huespedId: 0,
      fechaEntrada: hoy,
      fechaSalida: manana,
    });
    this.fechaEntradaValor.set(hoy);
    this.fechaSalidaValor.set(manana);
    this.habitacionesSeleccionadas.set([]);
    this.errorGuardar.set(null);
    this.errorCatalogos.set(null);
    this.modalCrearAbierto.set(true);
    this.cargarCatalogos();
  }

  cerrarModalCrear(): void {
    if (this.guardando()) return;
    this.modalCrearAbierto.set(false);
    this.errorGuardar.set(null);
  }

  cargarCatalogos(): void {
    this.cargandoCatalogos.set(true);
    this.errorCatalogos.set(null);

    forkJoin({
      huespedes: this.huespedService.listar(),
      habitaciones: this.habitacionService.listar(),
    }).subscribe({
      next: ({ huespedes, habitaciones }) => {
        this.huespedes.set(huespedes);
        this.habitaciones.set(habitaciones);
        this.cargandoCatalogos.set(false);
      },
      error: (err: Error) => {
        this.errorCatalogos.set(err.message);
        this.cargandoCatalogos.set(false);
      },
    });
  }

  alCambiarFechaEntrada(valor: string): void {
    this.formReserva.controls.fechaEntrada.setValue(valor);
    this.fechaEntradaValor.set(valor);
  }

  alCambiarFechaSalida(valor: string): void {
    this.formReserva.controls.fechaSalida.setValue(valor);
    this.fechaSalidaValor.set(valor);
  }

  estaSeleccionada(habitacionId: number): boolean {
    return this.habitacionesSeleccionadas().includes(habitacionId);
  }

  alternarHabitacion(habitacionId: number): void {
    this.habitacionesSeleccionadas.update(actuales =>
      actuales.includes(habitacionId)
        ? actuales.filter(id => id !== habitacionId)
        : [...actuales, habitacionId]
    );
  }

  guardarReserva(): void {
    this.formReserva.markAllAsTouched();
    if (
      this.formReserva.invalid ||
      this.fechasInvalidas() ||
      this.habitacionesSeleccionadas().length === 0
    ) {
      return;
    }

    this.guardando.set(true);
    this.errorGuardar.set(null);

    const { huespedId, fechaEntrada, fechaSalida } = this.formReserva.getRawValue();

    this.reservaService
      .crear({
        huespedId: Number(huespedId),
        fechaEntrada,
        fechaSalida,
        habitacionIds: this.habitacionesSeleccionadas(),
      })
      .subscribe({
        next: res => {
          this.reservas.update(lista => [res.result, ...lista]);
          this.guardando.set(false);
          this.modalCrearAbierto.set(false);
          this.mostrarExito(res.message);
        },
        error: (err: Error) => {
          this.errorGuardar.set(err.message);
          this.guardando.set(false);
        },
      });
  }

  verDetalle(reserva: Reserva): void {
    this.reservaDetalle.set(reserva);
  }

  cerrarDetalle(): void {
    this.reservaDetalle.set(null);
  }

  confirmarCancelar(reserva: Reserva): void {
    this.errorCancelar.set(null);
    this.reservaACancelar.set(reserva);
  }

  cerrarModalCancelar(): void {
    if (this.cancelando()) return;
    this.reservaACancelar.set(null);
    this.errorCancelar.set(null);
  }

  ejecutarCancelar(): void {
    const objetivo = this.reservaACancelar();
    if (!objetivo) return;

    this.cancelando.set(true);
    this.errorCancelar.set(null);

    this.reservaService.cancelar(objetivo.id).subscribe({
      next: res => {
        this.reservas.update(lista =>
          lista.map(item => (item.id === objetivo.id ? res.result : item))
        );
        this.cancelando.set(false);
        this.reservaACancelar.set(null);
        this.mostrarExito(res.message);
      },
      error: (err: Error) => {
        this.errorCancelar.set(err.message);
        this.cancelando.set(false);
      },
    });
  }

  nochesDe(reserva: Reserva): number {
    if (reserva.detalles?.length) {
      return reserva.detalles[0].noches;
    }
    const inicio = new Date(`${reserva.fechaEntrada}T00:00:00`).getTime();
    const fin = new Date(`${reserva.fechaSalida}T00:00:00`).getTime();
    return Math.max(1, Math.round((fin - inicio) / 86_400_000));
  }

  private redondear(valor: number): number {
    return Math.round((valor + Number.EPSILON) * 100) / 100;
  }

  private mostrarExito(mensaje: string): void {
    this.mensajeExito.set(mensaje);
    setTimeout(() => {
      if (this.mensajeExito() === mensaje) {
        this.mensajeExito.set(null);
      }
    }, 4500);
  }

  private static hoyISO(): string {
    const d = new Date();
    const anio = d.getFullYear();
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }

  private static sumarDiasISO(fechaISO: string, dias: number): string {
    const d = new Date(`${fechaISO}T00:00:00`);
    d.setDate(d.getDate() + dias);
    const anio = d.getFullYear();
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }
}