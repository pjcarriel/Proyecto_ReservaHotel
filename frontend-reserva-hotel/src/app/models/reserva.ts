export type EstadoReserva = 'CONFIRMADA' | 'CANCELADA';

export interface DetalleReservaItem {
  habitacionId: number;
  habitacionNumero: string;
  tipoHabitacionNombre: string;
  noches: number;
  precioNoche: number;
  subtotal: number;
}

export interface Reserva {
  id: number;
  numero: string;
  huespedId: number;
  huespedCedula: string;
  huespedNombre: string;
  fechaEntrada: string;
  fechaSalida: string;
  estado: EstadoReserva;
  detalles: DetalleReservaItem[];
  subtotal: number;
  iva: number;
  total: number;
  creado: string;
  modificado: string;
}

export interface ReservaRequest {
  huespedId: number;
  fechaEntrada: string;
  fechaSalida: string;
  habitacionIds: number[];
}