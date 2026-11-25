// Datos mínimos que el Dashboard lee de cada endpoint (GET /habitacion, /reserva y /huesped)

export interface HabitacionResumen {
  id: number;
  numero: string;
  estado: 'DISPONIBLE' | 'MANTENIMIENTO';
}

export interface ReservaResumen {
  id: number;
  numero: string;
  huespedNombre: string;
  fechaEntrada: string;   // yyyy-MM-dd
  fechaSalida: string;    // yyyy-MM-dd
  estado: 'CONFIRMADA' | 'CANCELADA';
  total: number;
  detalles: { habitacionId: number; habitacionNumero: string }[];
}

// Una llegada o salida del día
export interface Movimiento {
  numero: string;
  huesped: string;
  habitaciones: string;
  total: number;
}

// Resumen calculado para la pantalla de inicio (null = ese dato no se pudo cargar)
export interface ResumenDashboard {
  habitaciones: number | null;
  disponibles: number | null;
  reservasHoy: number | null;
  huespedes: number | null;
  ocupadas: number | null;
  mantenimiento: number | null;
  ocupacion: number | null;  // porcentaje 0-100
  llegadas: Movimiento[] | null;
  salidas: Movimiento[] | null;
}
