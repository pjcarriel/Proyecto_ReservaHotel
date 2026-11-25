import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ReservasComponent } from './reservas-component';
import { environment } from '../../../environments/environment';
import { Reserva } from '../../models/reserva';
import { Huesped } from '../../models/huesped';
import { Habitacion } from '../../models/habitacion';

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

  const mockHuespedes: Huesped[] = [
    {
      id: 1,
      cedula: '1710034065',
      nombres: 'Ana María',
      apellidos: 'Pérez Gómez',
      email: 'ana@mail.com',
      telefono: '0991234567',
      creado: '2026-11-23T10:00:00',
      modificado: '2026-11-23T10:00:00',
    },
  ];

  const mockHabitaciones: Habitacion[] = [
    {
      id: 10,
      numero: '201',
      piso: 2,
      precioNoche: 80,
      estado: 'DISPONIBLE',
      tipoHabitacionId: 1,
      tipoHabitacionNombre: 'Doble',
      creado: '2026-11-23T10:00:00',
      modificado: '2026-11-23T10:00:00',
    },
    {
      id: 11,
      numero: '202',
      piso: 2,
      precioNoche: 90,
      estado: 'MANTENIMIENTO',
      tipoHabitacionId: 2,
      tipoHabitacionNombre: 'Suite',
      creado: '2026-11-23T10:00:00',
      modificado: '2026-11-23T10:00:00',
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

  it('calcula noches, subtotal, IVA (15%) y total al crear una reserva y envía POST', () => {
    const fixture = TestBed.createComponent(ReservasComponent);
    fixture.detectChanges();

    http.expectOne(`${environment.apiURL}/reserva`)
      .flush({ message: 'ok', result: [] });
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    comp.abrirModalCrear();

    http.expectOne(`${environment.apiURL}/huesped`)
      .flush({ message: 'ok', result: mockHuespedes });
    http.expectOne(`${environment.apiURL}/habitacion`)
      .flush({ message: 'ok', result: mockHabitaciones });
    fixture.detectChanges();

    expect(comp.habitacionesDisponibles().length).toBe(1);

    comp.formReserva.controls.huespedId.setValue(1);
    comp.alCambiarFechaEntrada('2026-12-01');
    comp.alCambiarFechaSalida('2026-12-04');
    comp.alternarHabitacion(10);

    expect(comp.nochesCalculadas()).toBe(3);
    expect(comp.subtotalCalculado()).toBe(240);
    expect(comp.ivaCalculado()).toBe(36);
    expect(comp.totalCalculado()).toBe(276);

    comp.guardarReserva();

    const req = http.expectOne(`${environment.apiURL}/reserva`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      huespedId: 1,
      fechaEntrada: '2026-12-01',
      fechaSalida: '2026-12-04',
      habitacionIds: [10],
    });

    req.flush({
      message: 'Reserva creada correctamente',
      result: { ...listaInicial[0], id: 99, total: 276 },
    });
    fixture.detectChanges();

    expect(comp.modalCrearAbierto()).toBe(false);
    expect(comp.reservas().length).toBe(1);
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