export type EstadoHabitacion = 'DISPONIBLE' | 'MANTENIMIENTO';

export interface HabitacionRequest {
  numero: string;
  piso: number;
  precioNoche: number;
  estado: EstadoHabitacion;
  tipoHabitacionId: number;
}

export interface HabitacionResponse extends HabitacionRequest {
  id: number;
  tipoHabitacionNombre: string;
  creado: string;
  modificado: string;
}
