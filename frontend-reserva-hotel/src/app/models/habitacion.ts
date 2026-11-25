export type EstadoHabitacion = 'DISPONIBLE' | 'MANTENIMIENTO';

export interface Habitacion {
  id: number;
  numero: string;
  piso: number;
  precioNoche: number;
  estado: EstadoHabitacion;
  tipoHabitacionId: number;
  tipoHabitacionNombre: string;
  creado: string;
  modificado: string;
}