import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { IconComponent } from '../../layout/icon/icon';
import { ReservaService } from '../../services/reserva-service';
import { EstadoReserva, Reserva } from '../../models/reserva';

type FiltroEstado = 'TODAS' | EstadoReserva;

@Component({
  imports: [CurrencyPipe, PageHeaderComponent, IconComponent],
  selector: 'app-reservas-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './reservas-component.css',
  templateUrl: './reservas-component.html',
})
export class ReservasComponent implements OnInit {
  private reservaService = inject(ReservaService);

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

  private mostrarExito(mensaje: string): void {
    this.mensajeExito.set(mensaje);
    setTimeout(() => {
      if (this.mensajeExito() === mensaje) {
        this.mensajeExito.set(null);
      }
    }, 4000);
  }
}