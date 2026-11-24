import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { UsuariosComponent } from './usuarios-component';
import { environment } from '../../../environments/environment';
import { LoginService } from '../../services/login-service';
import { Rol } from '../../models/rol';

const URL = `${environment.apiURL}/usuario`;
const LISTA = [
  { id: 1, username: 'admin', rol: 'ADMIN', activo: true, creado: '2026-11-20T10:00:00', modificado: '' },
  { id: 2, username: 'recep1', rol: 'RECEPCIONISTA', activo: true, creado: '2026-11-21T10:00:00', modificado: '' },
];

describe('UsuariosComponent', () => {
  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [UsuariosComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    TestBed.inject(LoginService).usuarioSession.set({ token: 't', username: 'admin', rol: Rol.ADMIN });
  });

  async function crear() {
    const fixture = TestBed.createComponent(UsuariosComponent);
    fixture.detectChanges();
    TestBed.inject(HttpTestingController).expectOne(URL).flush({ message: 'ok', result: LISTA });
    await fixture.whenStable();
    fixture.detectChanges();
    return fixture;
  }

  it('muestra los usuarios y el resumen', async () => {
    const fixture = await crear();
    const html = fixture.nativeElement as HTMLElement;

    expect(html.querySelectorAll('tbody tr').length).toBe(2);
    expect(html.querySelector('.dato-valor')?.textContent).toContain('2');
  });

  it('no deja desactivar al usuario que inició sesión', async () => {
    const fixture = await crear();
    const botones = (fixture.nativeElement as HTMLElement).querySelectorAll<HTMLButtonElement>('button[aria-label^="Desactivar"]');

    expect(botones[0].getAttribute('aria-label')).toBe('Desactivar admin');
    expect(botones[0].disabled).toBe(true);
    expect(botones[1].disabled).toBe(false);
  });

  it('desactivar envía el PATCH y actualiza la fila', async () => {
    const fixture = await crear();
    const html = fixture.nativeElement as HTMLElement;
    html.querySelector<HTMLButtonElement>('button[aria-label="Desactivar recep1"]')!.click();

    const req = TestBed.inject(HttpTestingController).expectOne(`${URL}/2`);
    expect(req.request.body).toEqual({ activo: false });
    req.flush({ message: 'ok', result: { ...LISTA[1], activo: false } });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(html.querySelector('button[aria-label="Activar recep1"]')).toBeTruthy();
  });

  it('el formulario exige usuario y contraseña válidos', async () => {
    const fixture = await crear();
    const componente = fixture.componentInstance;
    componente.formulario.setValue({ username: 'ab', password: 'corta', rol: Rol.RECEPCIONISTA });

    componente.guardar();

    expect(componente.formulario.invalid).toBe(true);
    TestBed.inject(HttpTestingController).expectNone(URL);
  });
});
