import { Routes } from '@angular/router';
import { MenuComponent } from './layout/menu-component/menu-component';
import { DashboardComponent } from './components/dashboard-component/dashboard-component';
import { UsuariosComponent } from './components/usuarios-component/usuarios-component';
import { TiposHabitacionComponent } from './components/tipos-habitacion-component/tipos-habitacion-component';
import { HabitacionesComponent } from './components/habitaciones-component/habitaciones-component';
import { HuespedesComponent } from './components/huespedes-component/huespedes-component';
import { ReservasComponent } from './components/reservas-component/reservas-component';

// TODO (tarea #18): agregar la ruta 'login', authGuard en el layout y roleGuard por pantalla
export const routes: Routes = [
  {
    path: '',
    component: MenuComponent,
    children: [
      { path: '', component: DashboardComponent, title: 'Inicio · Hotel Reservas' },
      { path: 'reservas', component: ReservasComponent, title: 'Reservas · Hotel Reservas' },
      { path: 'huespedes', component: HuespedesComponent, title: 'Huéspedes · Hotel Reservas' },
      { path: 'habitaciones', component: HabitacionesComponent, title: 'Habitaciones · Hotel Reservas' },
      { path: 'tipos-habitacion', component: TiposHabitacionComponent, title: 'Tipos de habitación · Hotel Reservas' },
      { path: 'usuarios', component: UsuariosComponent, title: 'Usuarios · Hotel Reservas' },
    ],
  },
  { path: '**', redirectTo: '' },
];
