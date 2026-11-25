import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HabitacionService } from './habitacion-service';
import { HabitacionRequest, HabitacionResponse } from '../models/habitacion';
import { environment } from '../../environments/environment';

describe('HabitacionService', () => {
  let service: HabitacionService;
  let http: HttpTestingController;
  const url = `${environment.apiURL}/habitacion`;
  const datos: HabitacionRequest = {
    numero: '101',
    piso: 0,
    precioNoche: 45.5,
    estado: 'DISPONIBLE',
    tipoHabitacionId: 2,
  };
  const habitacion: HabitacionResponse = {
    ...datos,
    id: 7,
    tipoHabitacionNombre: 'Doble',
    creado: '2026-11-25T09:00:00',
    modificado: '2026-11-25T09:00:00',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(HabitacionService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());

  it('listar obtiene el tipo, estado y precio de ActionResponse.result', () => {
    service.listar().subscribe((lista) => expect(lista).toEqual([habitacion]));
    const req = http.expectOne(url);
    expect(req.request.method).toBe('GET');
    req.flush({ message: 'Encontradas', result: [habitacion] });
  });

  it('buscar consulta el registro por id antes de editar', () => {
    service.buscar(7).subscribe((resultado) => expect(resultado).toEqual(habitacion));
    const req = http.expectOne(`${url}/7`);
    expect(req.request.method).toBe('GET');
    req.flush({ message: 'Encontrada', result: habitacion });
  });

  it('crear envía el id numérico del tipo y el precio por noche', () => {
    service.crear(datos).subscribe((resultado) => expect(resultado.id).toBe(7));
    const req = http.expectOne(url);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(datos);
    req.flush({ message: 'Creada', result: habitacion }, { status: 201, statusText: 'Created' });
  });

  it('actualizar envía PUT al id de la habitación', () => {
    const cambios = { ...datos, estado: 'MANTENIMIENTO' as const, tipoHabitacionId: 3 };
    service
      .actualizar(7, cambios)
      .subscribe((resultado) => expect(resultado.estado).toBe('MANTENIMIENTO'));
    const req = http.expectOne(`${url}/7`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(cambios);
    req.flush({ message: 'Actualizada', result: { ...habitacion, ...cambios } });
  });

  it('eliminar envía DELETE y extrae el resultado nulo', () => {
    service.eliminar(7).subscribe((resultado) => expect(resultado).toBeNull());
    const req = http.expectOne(`${url}/7`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ message: 'Eliminada', result: null });
  });

  it('propaga el conflicto por número duplicado', () => {
    let mensaje = '';
    service.crear(datos).subscribe({
      error: (error) => {
        mensaje = error.error.message;
      },
    });
    http
      .expectOne(url)
      .flush(
        { message: 'El número ya existe', result: null },
        { status: 409, statusText: 'Conflict' },
      );
    expect(mensaje).toContain('ya existe');
  });
});
