import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { UsuarioService } from './usuario-service';
import { environment } from '../../environments/environment';
import { Rol } from '../models/rol';
import { Usuario } from '../models/usuario';

const URL = `${environment.apiURL}/usuario`;
const ADMIN: Usuario = { id: 1, username: 'admin', rol: Rol.ADMIN, activo: true, creado: '', modificado: '' };

describe('UsuarioService', () => {
  let service: UsuarioService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(UsuarioService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('listar devuelve el result del ActionResponse', () => {
    let resultado: Usuario[] = [];
    service.listar().subscribe(lista => (resultado = lista));

    http.expectOne(URL).flush({ message: 'Registros encontrados: 1', result: [ADMIN] });

    expect(resultado).toEqual([ADMIN]);
  });

  it('crear y actualizar envían el body con POST y PUT', () => {
    const datos = { username: 'recep1', password: 'clave12345', rol: Rol.RECEPCIONISTA };
    service.crear(datos).subscribe();
    service.actualizar(5, datos).subscribe();

    const post = http.expectOne(r => r.method === 'POST' && r.url === URL);
    expect(post.request.body).toEqual(datos);
    post.flush({ message: 'ok', result: ADMIN });

    const put = http.expectOne(r => r.method === 'PUT' && r.url === `${URL}/5`);
    expect(put.request.body).toEqual(datos);
    put.flush({ message: 'ok', result: ADMIN });
  });

  it('cambiarEstado hace PATCH con {"activo": false}', () => {
    service.cambiarEstado(1, false).subscribe();

    const req = http.expectOne(`${URL}/1`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ activo: false });
    req.flush({ message: 'Estado del usuario modificado', result: { ...ADMIN, activo: false } });
  });
});
