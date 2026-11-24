import { Rol } from '../../models/rol';

// Una opción del menú
export interface OpcionMenu {
  texto: string;
  descripcion: string;
  ruta: string;
  icono: string;      // nombre del ícono (ver layout/icon/icon.ts)
  roles: Rol[];       // quién puede ver esta opción (se aplica cuando exista el login)
  listo: boolean;     // false = la pantalla todavía no existe
}

// Un grupo de opciones (título + opciones)
export interface SeccionMenu {
  titulo: string;
  opciones: OpcionMenu[];
}

export const TODOS = [Rol.ADMIN, Rol.RECEPCIONISTA];
export const SOLO_ADMIN = [Rol.ADMIN];

// Cada responsable cambia "listo: true" cuando termine su pantalla
export const SECCIONES_MENU: SeccionMenu[] = [
  {
    titulo: '',
    opciones: [
      { texto: 'Inicio', descripcion: 'Resumen del hotel', icono: 'inicio', ruta: '/', roles: TODOS, listo: true },
    ],
  },
  {
    titulo: 'Recepción',
    opciones: [
      { texto: 'Reservas', descripcion: 'Crear y cancelar reservas', icono: 'reservas', ruta: '/reservas', roles: TODOS, listo: false },
      { texto: 'Huéspedes', descripcion: 'Registro de huéspedes', icono: 'huespedes', ruta: '/huespedes', roles: TODOS, listo: false },
    ],
  },
  {
    titulo: 'Hotel',
    opciones: [
      { texto: 'Habitaciones', descripcion: 'Habitaciones y su estado', icono: 'habitaciones', ruta: '/habitaciones', roles: TODOS, listo: false },
      { texto: 'Tipos de habitación', descripcion: 'Categorías y capacidad', icono: 'tipos', ruta: '/tipos-habitacion', roles: TODOS, listo: true },
    ],
  },
  {
    titulo: 'Administración',
    opciones: [
      { texto: 'Usuarios', descripcion: 'Cuentas y roles del sistema', icono: 'usuarios', ruta: '/usuarios', roles: SOLO_ADMIN, listo: false },
    ],
  },
];
