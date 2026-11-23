import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { LoginService } from './login-service';
import { environment } from '../../environments/environment';
import { Rol } from '../models/rol';

function tokenValido(): string {
  const payload = { sub: 'admin', rol: 'ADMIN', iat: 0, exp: Math.floor(Date.now() / 1000) + 3600 };
  return `x.${btoa(JSON.stringify(payload))}.y`;
}

describe('LoginService', () => {
  let service: LoginService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(LoginService);
    http = TestBed.inject(HttpTestingController);
  });

  it('login guarda la sesión y queda autenticado', () => {
    const token = tokenValido();
    service.login({ username: 'admin', password: 'Admin2026*' }).subscribe();

    const req = http.expectOne(`${environment.apiURL}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'Sesión iniciada', result: { username: 'admin', rol: 'ADMIN', token } });

    expect(service.isAuthenticated()).toBe(true);
    expect(service.getToken()).toBe(token);
    expect(service.isRole([Rol.ADMIN])).toBe(true);
    expect(service.isRole([Rol.RECEPCIONISTA])).toBe(false);
    expect(JSON.parse(localStorage.getItem('sesion')!).username).toBe('admin');
  });

  it('logout borra la sesión', () => {
    service.login({ username: 'admin', password: 'x' }).subscribe();
    http.expectOne(`${environment.apiURL}/auth/login`)
      .flush({ message: 'ok', result: { username: 'admin', rol: 'ADMIN', token: tokenValido() } });

    service.logout();

    expect(service.isAuthenticated()).toBe(false);
    expect(localStorage.getItem('sesion')).toBeNull();
  });

  it('un token mal formado no cuenta como sesión', () => {
    service.usuarioSession.set({ username: 'admin', rol: Rol.ADMIN, token: 'no-es-un-jwt' });

    expect(service.isAuthenticated()).toBe(false);
  });
});
