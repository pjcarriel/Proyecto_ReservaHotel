import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { calcularResumen, DashboardService, fechaLocal } from './dashboard-service';
import { environment } from '../../environments/environment';
import { HabitacionResumen, ReservaResumen, ResumenDashboard } from '../models/dashboard';

const HOY = '2026-11-25';

const HABITACIONES: HabitacionResumen[] = [
  { id: 1, numero: '101', estado: 'DISPONIBLE' },
  { id: 2, numero: '102', estado: 'DISPONIBLE' },
  { id: 3, numero: '103', estado: 'DISPONIBLE' },
  { id: 4, numero: '104', estado: 'MANTENIMIENTO' },
];

function reserva(id: number, entrada: string, salida: string, habitacionId: number,
                 estado: 'CONFIRMADA' | 'CANCELADA' = 'CONFIRMADA'): ReservaResumen {
  return {
    id, numero: `RES-${id}`, huespedNombre: `Huésped ${id}`, fechaEntrada: entrada, fechaSalida: salida,
    estado, total: 115, detalles: [{ habitacionId, habitacionNumero: `10${habitacionId}` }],
  };
}

const RESERVAS: ReservaResumen[] = [
  reserva(1, HOY, '2026-11-27', 1),              // llega hoy, ocupa la 101
  reserva(2, '2026-11-23', HOY, 2),              // sale hoy, la 102 ya queda libre
  reserva(3, '2026-11-24', '2026-11-26', 3),     // sigue hospedado en la 103
  reserva(4, HOY, '2026-11-28', 2, 'CANCELADA'), // cancelada: no cuenta
];

describe('calcularResumen', () => {
  it('calcula los indicadores, la ocupación y el movimiento de hoy', () => {
    const r = calcularResumen(HABITACIONES, RESERVAS, [{}, {}, {}], HOY);

    expect(r.habitaciones).toBe(4);
    expect(r.mantenimiento).toBe(1);
    expect(r.ocupadas).toBe(2);        // 101 y 103
    expect(r.disponibles).toBe(1);     // 102
    expect(r.ocupacion).toBe(50);
    expect(r.reservasHoy).toBe(1);
    expect(r.huespedes).toBe(3);
    expect(r.llegadas?.map(m => m.numero)).toEqual(['RES-1']);
    expect(r.salidas?.map(m => m.numero)).toEqual(['RES-2']);
  });

  it('si fallan las reservas muestra solo lo que sí cargó', () => {
    const r = calcularResumen(HABITACIONES, null, [], HOY);

    expect(r.habitaciones).toBe(4);
    expect(r.mantenimiento).toBe(1);
    expect(r.huespedes).toBe(0);
    expect(r.disponibles).toBeNull();
    expect(r.ocupacion).toBeNull();
    expect(r.llegadas).toBeNull();
  });

  it('sin habitaciones la ocupación es 0', () => {
    expect(calcularResumen([], [], [], HOY).ocupacion).toBe(0);
  });
});

describe('DashboardService', () => {
  it('pide los tres listados y tolera que uno falle', () => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);
    let resultado: ResumenDashboard | undefined;

    TestBed.inject(DashboardService).resumen(HOY).subscribe(r => (resultado = r));

    http.expectOne(`${environment.apiURL}/habitacion`).flush({ message: 'ok', result: HABITACIONES });
    http.expectOne(`${environment.apiURL}/reserva`).flush({ message: 'ok', result: RESERVAS });
    http.expectOne(`${environment.apiURL}/huesped`).flush({ message: 'error' }, { status: 500, statusText: 'Error' });

    expect(resultado?.habitaciones).toBe(4);
    expect(resultado?.reservasHoy).toBe(1);
    expect(resultado?.huespedes).toBeNull();
    http.verify();
  });

  it('fechaLocal usa la fecha local en formato yyyy-MM-dd', () => {
    expect(fechaLocal(new Date(2026, 0, 5, 23, 30))).toBe('2026-01-05');
  });
});
