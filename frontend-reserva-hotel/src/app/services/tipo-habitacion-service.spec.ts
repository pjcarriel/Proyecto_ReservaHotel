import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TipoHabitacionService } from './tipo-habitacion-service';
import { TipoHabitacionRequest, TipoHabitacionResponse } from '../models/tipo-habitacion';
import { environment } from '../../environments/environment';

describe('TipoHabitacionService', () => {
  let service: TipoHabitacionService;
  let http: HttpTestingController;
  const url = `${environment.apiURL}/tipo-habitacion`;
  const datos: TipoHabitacionRequest = {
    nombre: 'Doble',
    descripcion: 'Habitación para dos personas',
    capacidad: 2,
  };
  const tipo: TipoHabitacionResponse = {
    ...datos,
    id: 7,
    creado: '2026-11-23T09:00:00',
    modificado: '2026-11-23T09:00:00',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(TipoHabitacionService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('listar obtiene los registros de ActionResponse.result', () => {
    service.listar().subscribe((resultado) => expect(resultado).toEqual([tipo]));
    const req = http.expectOne(url);
    expect(req.request.method).toBe('GET');
    req.flush({ message: 'Encontrados', result: [tipo] });
  });

  it('buscar consulta por id y conserva los datos de auditoría', () => {
    service.buscar(7).subscribe((resultado) => expect(resultado).toEqual(tipo));
    const req = http.expectOne(`${url}/7`);
    expect(req.request.method).toBe('GET');
    req.flush({ message: 'Encontrado', result: tipo });
  });

  it('crear envía únicamente los datos del formulario', () => {
    service.crear(datos).subscribe((resultado) => expect(resultado.id).toBe(7));
    const req = http.expectOne(url);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(datos);
    req.flush({ message: 'Creado', result: tipo }, { status: 201, statusText: 'Created' });
  });

  it('actualizar envía PUT al id correspondiente', () => {
    service.actualizar(7, datos).subscribe((resultado) => expect(resultado).toEqual(tipo));
    const req = http.expectOne(`${url}/7`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(datos);
    req.flush({ message: 'Actualizado', result: tipo });
  });

  it('eliminar envía DELETE y recibe el resultado nulo del backend', () => {
    service.eliminar(7).subscribe((resultado) => expect(resultado).toBeNull());
    const req = http.expectOne(`${url}/7`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ message: 'Eliminado', result: null });
  });

  it('propaga los conflictos para que se muestren en la pantalla', () => {
    let recibido: number | undefined;
    service.crear(datos).subscribe({ error: (error) => (recibido = error.status) });
    http
      .expectOne(url)
      .flush(
        { message: 'Nombre duplicado', result: null },
        { status: 409, statusText: 'Conflict' },
      );
    expect(recibido).toBe(409);
  });
});
