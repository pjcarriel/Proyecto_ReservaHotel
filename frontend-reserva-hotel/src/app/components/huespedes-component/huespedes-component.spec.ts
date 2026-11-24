import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HuespedesComponent } from './huespedes-component';
import { environment } from '../../../environments/environment';
import { Huesped } from '../../models/huesped';

describe('HuespedesComponent', () => {
  let http: HttpTestingController;

  const listaInicial: Huesped[] = [
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
    {
      id: 2,
      cedula: '0912345678',
      nombres: 'Carlos',
      apellidos: 'Mendoza',
      email: 'carlos@mail.com',
      telefono: null,
      creado: '2026-11-23T10:05:00',
      modificado: '2026-11-23T10:05:00',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HuespedesComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('carga y muestra los huéspedes en la tabla', () => {
    const fixture = TestBed.createComponent(HuespedesComponent);
    fixture.detectChanges();

    http.expectOne(`${environment.apiURL}/huesped`)
      .flush({ message: 'Registros encontrados: 2', result: listaInicial });
    fixture.detectChanges();

    const filas = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');
    expect(filas.length).toBe(2);
    expect(filas[0].textContent).toContain('1710034065');
    expect(filas[1].textContent).toContain('Carlos');
  });

  it('filtra huéspedes por cédula o nombre', () => {
    const fixture = TestBed.createComponent(HuespedesComponent);
    fixture.detectChanges();

    http.expectOne(`${environment.apiURL}/huesped`)
      .flush({ message: 'ok', result: listaInicial });
    fixture.detectChanges();

    fixture.componentInstance.filtro.set('carlos');
    fixture.detectChanges();

    const filas = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');
    expect(filas.length).toBe(1);
    expect(filas[0].textContent).toContain('0912345678');
  });

  it('registra un nuevo huésped desde el modal', () => {
    const fixture = TestBed.createComponent(HuespedesComponent);
    fixture.detectChanges();

    http.expectOne(`${environment.apiURL}/huesped`)
      .flush({ message: 'ok', result: [] });
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    comp.abrirNuevo();
    comp.frmHuesped.setValue({
      cedula: '1722334455',
      nombres: 'Lucía',
      apellidos: 'Torres',
      email: 'lucia@mail.com',
      telefono: '0987654321',
    });
    comp.guardar();

    const req = http.expectOne(`${environment.apiURL}/huesped`);
    expect(req.request.method).toBe('POST');
    req.flush({
      message: 'Huésped creado correctamente',
      result: {
        id: 3,
        cedula: '1722334455',
        nombres: 'Lucía',
        apellidos: 'Torres',
        email: 'lucia@mail.com',
        telefono: '0987654321',
        creado: '2026-11-23T11:00:00',
        modificado: '2026-11-23T11:00:00',
      },
    });
    fixture.detectChanges();

    expect(comp.huespedes().length).toBe(1);
    expect(comp.modalAbierto()).toBe(false);
  });
});