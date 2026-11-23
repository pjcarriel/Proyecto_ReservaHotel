import { Routes } from '@angular/router';
import { LoginComponent } from './components/login-component/login-component';
import { MenuComponent } from './layout/menu-component/menu-component';
import { DashboardComponent } from './components/dashboard-component/dashboard-component';
import { UsuariosComponent } from './components/usuarios-component/usuarios-component';
import { TiposHabitacionComponent } from './components/tipos-habitacion-component/tipos-habitacion-component';
import { HabitacionesComponent } from './components/habitaciones-component/habitaciones-component';
import { HuespedesComponent } from './components/huespedes-component/huespedes-component';
import { ReservasComponent } from './components/reservas-component/reservas-component';
import { authGuard } from './core/auth-guard';
import { roleGuard } from './core/role-guard';
import { Rol } from './models/rol';

const TODOS = [Rol.ADMIN, Rol.RECEPCIONISTA];

export const routes: Routes = [
  { path: 'login', component: LoginComponent, title: 'Iniciar sesión · Hotel Reservas' },
  {
    path: '',
    component: MenuComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '', component: DashboardComponent, title: 'Inicio · Hotel Reservas',
        canActivate: [roleGuard], data: { roles: TODOS }
      },
      {
        path: 'reservas', component: ReservasComponent, title: 'Reservas · Hotel Reservas',
        canActivate: [roleGuard], data: { roles: TODOS }
      },
      {
        path: 'huespedes', component: HuespedesComponent, title: 'Huéspedes · Hotel Reservas',
        canActivate: [roleGuard], data: { roles: TODOS }
      },
      {
        path: 'habitaciones', component: HabitacionesComponent, title: 'Habitaciones · Hotel Reservas',
        canActivate: [roleGuard], data: { roles: TODOS }
      },
      {
        path: 'tipos-habitacion', component: TiposHabitacionComponent, title: 'Tipos de habitación · Hotel Reservas',
        canActivate: [roleGuard], data: { roles: TODOS }
      },
      {
        path: 'usuarios', component: UsuariosComponent, title: 'Usuarios · Hotel Reservas',
        canActivate: [roleGuard], data: { roles: [Rol.ADMIN] }
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
