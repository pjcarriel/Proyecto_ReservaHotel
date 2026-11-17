import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
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
  imports: [RouterLink, IconComponent],
  selector: 'app-dashboard-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './dashboard-component.css',
  templateUrl: './dashboard-component.html',
})
export class DashboardComponent {
  readonly saludo = DashboardComponent.saludoSegunHora(new Date().getHours());

  // Acciones principales de la portada (se activan cuando su pantalla esté lista)
  readonly nuevaReserva = opcion('/reservas');
  readonly nuevoHuesped = opcion('/huespedes');

  // TODO (tarea #24): cargar los valores reales desde los servicios
  readonly indicadores: Indicador[] = [
    { titulo: 'Habitaciones', icono: 'habitaciones', tono: 'marino', valor: null, detalle: 'Total registradas' },
    { titulo: 'Disponibles', icono: 'cama-libre', tono: 'exito', valor: null, detalle: 'Listas para reservar' },
    { titulo: 'Reservas de hoy', icono: 'reservas', tono: 'laton', valor: null, detalle: 'Entradas del día' },
    { titulo: 'Huéspedes', icono: 'huespedes', tono: 'azul', valor: null, detalle: 'Registrados en el sistema' },
  ];

  // Movimiento del día: llegadas o salidas
  movimiento = signal<'llegadas' | 'salidas'>('llegadas');

  // Ocupación de hoy (porcentaje de habitaciones ocupadas)
  readonly ocupacion = signal<number | null>(null);
  readonly leyenda: Leyenda[] = [
    { texto: 'Ocupadas', clase: 'ocupadas', valor: null },
    { texto: 'Disponibles', clase: 'disponibles', valor: null },
    { texto: 'Mantenimiento', clase: 'mantenimiento', valor: null },
  ];

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
