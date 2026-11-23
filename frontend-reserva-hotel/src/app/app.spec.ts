import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { App } from './app';
import { routes } from './app.routes';
import { environment } from '../environments/environment';
import { jwtInterceptor } from './core/jwt-interceptor';
import { errorInterceptor } from './core/error-interceptor';

// Token de prueba con el formato del backend (solo se lee el payload, no la firma)
function tokenDePrueba(rol: string, expiraEnSegundos: number): string {
  const payload = { sub: 'admin', rol, iat: 0, exp: Math.floor(Date.now() / 1000) + expiraEnSegundos };
  return `x.${btoa(JSON.stringify(payload))}.y`;
}

function guardarSesion(rol: string, expiraEnSegundos = 3600): string {
  const token = tokenDePrueba(rol, expiraEnSegundos);
  localStorage.setItem('sesion', JSON.stringify({ token, username: 'admin', rol }));
  return token;
}

describe('App', () => {
  beforeEach(async () => {
    localStorage.clear();
    TestBed.resetTestingModule();
  });

  function configurar() {
    return TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter(routes),
        provideHttpClient(withInterceptors([jwtInterceptor, errorInterceptor])),
        provideHttpClientTesting(),
      ],
    }).compileComponents();
  }

  it('should create the app', async () => {
    await configurar();
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('sin sesión manda al login', async () => {
    await configurar();
    const fixture = TestBed.createComponent(App);
    const router = TestBed.inject(Router);
    await router.navigateByUrl('/');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(router.url).toBe('/login');
    expect((fixture.nativeElement as HTMLElement).querySelector('app-login-component form')).toBeTruthy();
  });

  it('con un token vencido también manda al login', async () => {
    guardarSesion('ADMIN', -60);
    await configurar();
    const fixture = TestBed.createComponent(App);
    const router = TestBed.inject(Router);
    await router.navigateByUrl('/');
    await fixture.whenStable();

    expect(router.url).toBe('/login');
  });

  it('con sesión muestra el menú, el dashboard y envía el token', async () => {
    const token = guardarSesion('ADMIN');
    await configurar();
    const fixture = TestBed.createComponent(App);
    await TestBed.inject(Router).navigateByUrl('/');
    await fixture.whenStable();
    fixture.detectChanges();

    const html = fixture.nativeElement as HTMLElement;
    expect(html.querySelector('.marca-nombre')?.textContent).toContain('Hotel Reservas');
    expect(html.querySelectorAll('nav li').length).toBe(6);
    expect(html.querySelector('app-dashboard-component')).toBeTruthy();

    // El menú consulta la salud del backend y el interceptor agrega el token
    const peticion = TestBed.inject(HttpTestingController).expectOne(`${environment.apiURL}/health`);
    expect(peticion.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
  });

  it('un RECEPCIONISTA no puede entrar a Usuarios', async () => {
    guardarSesion('RECEPCIONISTA');
    await configurar();
    const fixture = TestBed.createComponent(App);
    const router = TestBed.inject(Router);
    await router.navigateByUrl('/usuarios');
    await fixture.whenStable();

    expect(router.url).toBe('/');
  });
});
