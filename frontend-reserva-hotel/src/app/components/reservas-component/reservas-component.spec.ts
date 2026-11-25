import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ReservasComponent } from './reservas-component';
import { environment } from '../../../environments/environment';
import { Reserva } from '../../models/reserva';

describe('ReservasComponent', () => {
  let http: HttpTestingController;

  const listaInicial: Reserva[] = [
    {
      id: 1,
      numero: 'RES-20261124001',
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
      creado: '2026-11-24T10:00:00',
      modificado: '2026-11-24T10:00:00',
    },
    {
      id: 2,
      numero: 'RES-20261124002',
      huespedId: 2,
      huespedCedula: '0912345678',
      huespedNombre: 'Carlos Mendoza',
      fechaEntrada: '2026-12-10',
      fechaSalida: '2026-12-12',
      estado: 'CANCELADA',
      detalles: [],
      subtotal: 100,
      iva: 15,
      total: 115,
      creado: '2026-11-24T10:05:00',
      modificado: '2026-11-24T10:05:00',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservasComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('carga y muestra las reservas con número, huésped, fechas, total y estado', () => {
    const fixture = TestBed.createComponent(ReservasComponent);
    fixture.detectChanges();

    http.expectOne(`${environment.apiURL}/reserva`)
      .flush({ message: 'Registros encontrados: 2', result: listaInicial });
    fixture.detectChanges();

    const filas = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');
    expect(filas.length).toBe(2);
    expect(filas[0].textContent).toContain('RES-20261124001');
    expect(filas[0].textContent).toContain('Ana María Pérez Gómez');
    expect(filas[0].textContent).toContain('CONFIRMADA');
    expect(filas[1].textContent).toContain('CANCELADA');
  });

  it('cancela una reserva confirmada mediante PATCH', () => {
    const fixture = TestBed.createComponent(ReservasComponent);
    fixture.detectChanges();

    http.expectOne(`${environment.apiURL}/reserva`)
      .flush({ message: 'ok', result: listaInicial });
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    comp.confirmarCancelar(listaInicial[0]);
    comp.ejecutarCancelar();

    const req = http.expectOne(`${environment.apiURL}/reserva/1/cancelar`);
    expect(req.request.method).toBe('PATCH');
    req.flush({
      message: 'Reserva cancelada correctamente',
      result: { ...listaInicial[0], estado: 'CANCELADA' },
    });
    fixture.detectChanges();

    expect(comp.reservas()[0].estado).toBe('CANCELADA');
    expect(comp.reservaACancelar()).toBeNull();
  });
});