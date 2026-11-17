import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../../layout/icon/icon';
import { SECCIONES_MENU } from '../../layout/menu-component/menu-opciones';

interface Indicador {
  titulo: string;
  icono: string;
  valor: number | null;   // null = todavía no hay dato del backend
  detalle: string;
}

@Component({
  imports: [RouterLink, IconComponent],
  selector: 'app-dashboard-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './dashboard-component.css',
  templateUrl: './dashboard-component.html',
})
export class DashboardComponent {
  readonly saludo = DashboardComponent.saludoSegunHora(new Date().getHours());

  // TODO (tarea #24): cargar los valores reales desde los servicios
  readonly indicadores: Indicador[] = [
    { titulo: 'Habitaciones', icono: 'habitaciones', valor: null, detalle: 'Total registradas' },
    { titulo: 'Disponibles', icono: 'cama-libre', valor: null, detalle: 'Listas para reservar' },
    { titulo: 'Reservas de hoy', icono: 'reservas', valor: null, detalle: 'Entradas del día' },
    { titulo: 'Huéspedes', icono: 'huespedes', valor: null, detalle: 'Registrados en el sistema' },
  ];

  // Accesos rápidos: todas las opciones del menú menos Inicio
  readonly accesos = SECCIONES_MENU.flatMap(s => s.opciones).filter(o => o.ruta !== '/');

  private static saludoSegunHora(hora: number): string {
    if (hora < 12) return 'Buenos días';
    if (hora < 19) return 'Buenas tardes';
    return 'Buenas noches';
  }
}
