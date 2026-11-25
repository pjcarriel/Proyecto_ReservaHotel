import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ReservaService } from './reserva-service';
import { environment } from '../../environments/environment';
import { Reserva } from '../models/reserva';

describe('ReservaService', () => {
  let service: ReservaService;
  let http: HttpTestingController;

  const mockReserva: Reserva = {
    id: 1,
    numero: 'RES-20261124120000001',
    huespedId: 1,
    huespedCedula: '1710034065',
    huespedNombre: 'Ana María Pérez Gómez',
    fechaEntrada: '2026-12-01',
    fechaSalida: '2026-12-04',
    estado: 'CONFIRMADA',
    detalles: [
      {
        habitacionId: 1,
        habitacionNumero: '101',
        tipoHabitacionNombre: 'Simple',
        noches: 3,
        precioNoche: 50,
        subtotal: 150,
      },
    ],
    subtotal: 150,
    iva: 22.5,
    total: 172.5,
    creado: '2026-11-24T12:00:00',
    modificado: '2026-11-24T12:00:00',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ReservaService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('listar devuelve el arreglo de reservas', () => {
    let resultado: Reserva[] = [];
    service.listar().subscribe(lista => (resultado = lista));

    const req = http.expectOne(`${environment.apiURL}/reserva`);
    expect(req.request.method).toBe('GET');
    req.flush({ message: 'Registros encontrados: 1', result: [mockReserva] });

    expect(resultado.length).toBe(1);
    expect(resultado[0].numero).toBe('RES-20261124120000001');
  });

  it('cancelar envía PATCH a /reserva/{id}/cancelar y retorna reserva actualizada', () => {
    let estadoActualizado = '';
    service.cancelar(1).subscribe(res => (estadoActualizado = res.result.estado));

    const req = http.expectOne(`${environment.apiURL}/reserva/1/cancelar`);
    expect(req.request.method).toBe('PATCH');
    req.flush({
      message: 'Reserva cancelada correctamente',
      result: { ...mockReserva, estado: 'CANCELADA' },
    });

    expect(estadoActualizado).toBe('CANCELADA');
  });
});