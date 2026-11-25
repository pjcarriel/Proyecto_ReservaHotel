import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, forkJoin, map, Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActionResponse } from '../models/action-response';
import { HabitacionResumen, Movimiento, ReservaResumen, ResumenDashboard } from '../models/dashboard';

// Fecha local en formato yyyy-MM-dd (igual que LocalDate del backend)
export function fechaLocal(fecha = new Date()): string {
  const mes = String(fecha.getMonth() + 1).padStart(2, '0');
  const dia = String(fecha.getDate()).padStart(2, '0');
  return `${fecha.getFullYear()}-${mes}-${dia}`;
}

@Service()
export class DashboardService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiURL;

  // Carga los tres listados a la vez. Si uno falla, sus indicadores quedan en null y el resto se muestra.
  resumen(hoy = fechaLocal()): Observable<ResumenDashboard> {
    return forkJoin({
      habitaciones: this.listar<HabitacionResumen>('habitacion'),
      reservas: this.listar<ReservaResumen>('reserva'),
      huespedes: this.listar<unknown>('huesped'),
    }).pipe(map(datos => calcularResumen(datos.habitaciones, datos.reservas, datos.huespedes, hoy)));
  }

  private listar<T>(ruta: string): Observable<T[] | null> {
    return this.http.get<ActionResponse<T[]>>(`${this.api}/${ruta}`).pipe(
      map(res => res.result),
      catchError(() => of(null)),
    );
  }
}

export function calcularResumen(
  habitaciones: HabitacionResumen[] | null,
  reservas: ReservaResumen[] | null,
  huespedes: unknown[] | null,
  hoy: string,
): ResumenDashboard {
  const confirmadas = reservas?.filter(r => r.estado === 'CONFIRMADA') ?? null;

  // Una habitación está ocupada hoy si tiene una reserva confirmada con entrada <= hoy < salida
  const idsOcupadas = confirmadas === null ? null : new Set(
    confirmadas
      .filter(r => r.fechaEntrada <= hoy && hoy < r.fechaSalida)
      .flatMap(r => r.detalles.map(d => d.habitacionId)),
  );

  let ocupadas: number | null = null;
  let mantenimiento: number | null = null;
  let disponibles: number | null = null;
  let ocupacion: number | null = null;

  if (habitaciones !== null) {
    mantenimiento = habitaciones.filter(h => h.estado === 'MANTENIMIENTO').length;
    if (idsOcupadas !== null) {
      ocupadas = habitaciones.filter(h => h.estado === 'DISPONIBLE' && idsOcupadas.has(h.id)).length;
      disponibles = habitaciones.length - mantenimiento - ocupadas;
      ocupacion = habitaciones.length === 0 ? 0 : Math.round((ocupadas * 100) / habitaciones.length);
    }
  }

  const movimiento = (r: ReservaResumen): Movimiento => ({
    numero: r.numero,
    huesped: r.huespedNombre,
    habitaciones: r.detalles.map(d => d.habitacionNumero).join(', '),
    total: r.total,
  });
  const llegadas = confirmadas?.filter(r => r.fechaEntrada === hoy).map(movimiento) ?? null;
  const salidas = confirmadas?.filter(r => r.fechaSalida === hoy).map(movimiento) ?? null;

  return {
    habitaciones: habitaciones?.length ?? null,
    disponibles,
    reservasHoy: llegadas?.length ?? null,
    huespedes: huespedes?.length ?? null,
    ocupadas,
    mantenimiento,
    ocupacion,
    llegadas,
    salidas,
  };
}
