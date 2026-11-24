import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HuespedService } from './huesped-service';
import { environment } from '../../environments/environment';
import { Huesped } from '../models/huesped';

describe('HuespedService', () => {
  let service: HuespedService;
  let http: HttpTestingController;

  const mockHuesped: Huesped = {
    id: 1,
    cedula: '1710034065',
    nombres: 'Ana María',
    apellidos: 'Pérez Gómez',
    email: 'ana@mail.com',
    telefono: '0991234567',
    creado: '2026-11-23T10:00:00',
    modificado: '2026-11-23T10:00:00',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(HuespedService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('listar devuelve el arreglo de huéspedes', () => {
    let resultado: Huesped[] = [];
    service.listar().subscribe(lista => (resultado = lista));

    const req = http.expectOne(`${environment.apiURL}/huesped`);
    expect(req.request.method).toBe('GET');
    req.flush({ message: 'Registros encontrados: 1', result: [mockHuesped] });

    expect(resultado.length).toBe(1);
    expect(resultado[0].cedula).toBe('1710034065');
  });

  it('crear envía POST y retorna ActionResponse con el huésped creado', () => {
    let mensaje = '';
    service.crear({
      cedula: '1710034065',
      nombres: 'Ana María',
      apellidos: 'Pérez Gómez',
      email: 'ana@mail.com',
      telefono: '0991234567',
    }).subscribe(res => (mensaje = res.message));

    const req = http.expectOne(`${environment.apiURL}/huesped`);
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'Huésped creado correctamente', result: mockHuesped });

    expect(mensaje).toBe('Huésped creado correctamente');
  });

  it('actualizar envía PUT al id correspondiente', () => {
    service.actualizar(1, {
      cedula: '1710034065',
      nombres: 'Ana',
      apellidos: 'Pérez',
      email: 'ana@mail.com',
      telefono: null,
    }).subscribe();

    const req = http.expectOne(`${environment.apiURL}/huesped/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ message: 'Huésped actualizado correctamente', result: mockHuesped });
  });

  it('eliminar envía DELETE al id correspondiente', () => {
    service.eliminar(1).subscribe();

    const req = http.expectOne(`${environment.apiURL}/huesped/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ message: 'Huésped eliminado correctamente', result: null });
  });
});