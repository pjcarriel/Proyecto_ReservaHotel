import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { DashboardService } from '../../services/dashboard-service';
import { IconComponent } from '../../layout/icon/icon';
import { OpcionMenu, SECCIONES_MENU } from '../../layout/menu-component/menu-opciones';

type Tono = 'marino' | 'exito' | 'laton' | 'azul';

interface Indicador {
  titulo: string;
  icono: string;
  tono: Tono;
  valor: number | null;   // null = todavía no hay dato del backend
  detalle: string;
}

interface Leyenda {
  texto: string;
  clase: string;
  valor: number | null;
}

const OPCIONES = SECCIONES_MENU.flatMap(s => s.opciones);
const opcion = (ruta: string): OpcionMenu | undefined => OPCIONES.find(o => o.ruta === ruta);

@Component({
  imports: [RouterLink, IconComponent, CurrencyPipe],
  selector: 'app-dashboard-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './dashboard-component.css',
  templateUrl: './dashboard-component.html',
})
export class DashboardComponent {
  private readonly dashboardService = inject(DashboardService);

  readonly saludo = DashboardComponent.saludoSegunHora(new Date().getHours());

  // Acciones principales de la portada (se activan cuando su pantalla esté lista)
  readonly nuevaReserva = opcion('/reservas');
  readonly nuevoHuesped = opcion('/huespedes');

  // Datos reales (GET /habitacion, /reserva y /huesped)
  readonly cargando = signal(true);
  private readonly resumen = toSignal(
    this.dashboardService.resumen().pipe(finalize(() => this.cargando.set(false))),
    { initialValue: null },
  );

  readonly indicadores = computed<Indicador[]>(() => {
    const r = this.resumen();
    return [
      { titulo: 'Habitaciones', icono: 'habitaciones', tono: 'marino', valor: r?.habitaciones ?? null, detalle: 'Total registradas' },
      { titulo: 'Disponibles', icono: 'cama-libre', tono: 'exito', valor: r?.disponibles ?? null, detalle: 'Libres para hoy' },
      { titulo: 'Reservas de hoy', icono: 'reservas', tono: 'laton', valor: r?.reservasHoy ?? null, detalle: 'Entradas del día' },
      { titulo: 'Huéspedes', icono: 'huespedes', tono: 'azul', valor: r?.huespedes ?? null, detalle: 'Registrados en el sistema' },
    ];
  });

  // Movimiento del día: llegadas o salidas
  movimiento = signal<'llegadas' | 'salidas'>('llegadas');
  readonly listaMovimiento = computed(() => {
    const r = this.resumen();
    return this.movimiento() === 'llegadas' ? r?.llegadas ?? null : r?.salidas ?? null;
  });

  // Ocupación de hoy (porcentaje de habitaciones ocupadas)
  readonly ocupacion = computed(() => this.resumen()?.ocupacion ?? null);
  readonly leyenda = computed<Leyenda[]>(() => {
    const r = this.resumen();
    return [
      { texto: 'Ocupadas', clase: 'ocupadas', valor: r?.ocupadas ?? null },
      { texto: 'Disponibles', clase: 'disponibles', valor: r?.disponibles ?? null },
      { texto: 'Mantenimiento', clase: 'mantenimiento', valor: r?.mantenimiento ?? null },
    ];
  });

  // Largo del arco del anillo (circunferencia de r=52 ≈ 326.7)
  readonly circunferencia = 2 * Math.PI * 52;
  arco = computed(() => (this.circunferencia * (this.ocupacion() ?? 0)) / 100);

  // Accesos rápidos: todas las opciones del menú menos Inicio
  readonly accesos = OPCIONES.filter(o => o.ruta !== '/');

  private static saludoSegunHora(hora: number): string {
    if (hora < 12) return 'Buenos días';
    if (hora < 19) return 'Buenas tardes';
    return 'Buenas noches';
  }
}
