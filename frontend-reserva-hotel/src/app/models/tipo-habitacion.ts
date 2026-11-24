export interface TipoHabitacionRequest {
  nombre: string;
  descripcion: string;
  capacidad: number;
}

export interface TipoHabitacionResponse extends TipoHabitacionRequest {
  id: number;
  creado: string;
  modificado: string;
}
