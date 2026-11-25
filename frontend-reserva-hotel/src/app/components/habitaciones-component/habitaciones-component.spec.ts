import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';
import { HabitacionService } from '../../services/habitacion-service';
import { TipoHabitacionService } from '../../services/tipo-habitacion-service';
import { HabitacionRequest, HabitacionResponse } from '../../models/habitacion';
import { TipoHabitacionResponse } from '../../models/tipo-habitacion';
import { HabitacionesComponent } from './habitaciones-component';

const tipo: TipoHabitacionResponse = {
  id: 2,
  nombre: 'Doble',
  descripcion: 'Habitación confortable para dos personas',
  capacidad: 2,
  creado: '2026-11-25T09:00:00',
  modificado: '2026-11-25T09:00:00',
};
const habitacion: HabitacionResponse = {
  id: 1,
  numero: '101',
  piso: 1,
  precioNoche: 45.5,
  estado: 'DISPONIBLE',
  tipoHabitacionId: tipo.id,
  tipoHabitacionNombre: tipo.nombre,
  creado: '2026-11-25T09:00:00',
  modificado: '2026-11-25T09:00:00',
};

function servicioSimulado() {
  return {
    listar: vi.fn(() => of([habitacion])),
    buscar: vi.fn((_id: number) => of(habitacion)),
    crear: vi.fn((_datos: HabitacionRequest) => of({ ...habitacion, id: 3 })),
    actualizar: vi.fn((_id: number, _datos: HabitacionRequest) => of(habitacion)),
    eliminar: vi.fn((_id: number) => of(null)),
  };
}

describe('HabitacionesComponent', () => {
  let fixture: ComponentFixture<HabitacionesComponent>;
  let component: HabitacionesComponent;
  let service: ReturnType<typeof servicioSimulado>;
  let tiposService: {
    listar: ReturnType<typeof vi.fn<() => ReturnType<TipoHabitacionService['listar']>>>;
  };

  beforeEach(async () => {
    service = servicioSimulado();
    tiposService = { listar: vi.fn(() => of([tipo])) };
    await TestBed.configureTestingModule({
      imports: [HabitacionesComponent],
      providers: [
        provideRouter([]),
        { provide: HabitacionService, useValue: service },
        { provide: TipoHabitacionService, useValue: tiposService },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(HabitacionesComponent);
    component = fixture.componentInstance;
  });

  function iniciar(): void {
    fixture.detectChanges();
    // jsdom no implementa los métodos nativos; los recorridos en Chrome verifican los diálogos reales.
    for (const dialog of (fixture.nativeElement as HTMLElement).querySelectorAll('dialog')) {
      dialog.showModal = () => dialog.setAttribute('open', '');
      dialog.close = () => dialog.removeAttribute('open');
    }
  }
  function datosValidos(): void {
    component.formulario.setValue({
      numero: '  202  ',
      piso: 0,
      precioNoche: 50.25,
      estado: 'DISPONIBLE',
      tipoHabitacionId: 2,
    });
  }
  const abierto = () => document.querySelector('dialog[open]');

  it('muestra tipo y estado, y combina el filtro por estado con la búsqueda', () => {
    service.listar.mockReturnValue(
      of([
        habitacion,
        {
          ...habitacion,
          id: 2,
          numero: '102',
          estado: 'MANTENIMIENTO',
          tipoHabitacionNombre: 'Suite',
        },
      ]),
    );
    iniciar();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Doble');
    const filtro = (fixture.nativeElement as HTMLElement).querySelectorAll<HTMLButtonElement>(
      '.segmentos button',
    )[2];
    filtro.click();
    component.busqueda.set('suite');
    fixture.detectChanges();
    expect(component.habitacionesFiltradas().map((h) => h.numero)).toEqual(['102']);
    expect((fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr')).toHaveLength(1);
    component.limpiarFiltros();
    expect(component.habitacionesFiltradas()).toHaveLength(2);
  });

  it('carga el select desde TipoHabitacionService y no preselecciona otro tipo', () => {
    iniciar();
    component.nuevo();
    fixture.detectChanges();
    const select = (fixture.nativeElement as HTMLElement).querySelector<HTMLSelectElement>(
      '#habitacion-tipo',
    )!;
    expect(tiposService.listar).toHaveBeenCalledTimes(2);
    expect(select.textContent).toContain('Doble · 2 personas');
    expect(component.formulario.controls.tipoHabitacionId.value).toBeNull();
  });

  it.each([
    ['número vacío', { numero: '   ' }],
    ['número demasiado largo', { numero: 'x'.repeat(21) }],
    ['piso negativo', { piso: -1 }],
    ['piso decimal', { piso: 1.5 }],
    ['piso vacío', { piso: null }],
    ['precio cero', { precioNoche: 0 }],
    ['precio vacío', { precioNoche: null }],
    ['precio con tres decimales', { precioNoche: 12.345 }],
    ['precio fuera de rango', { precioNoche: 100000000 }],
    ['tipo sin seleccionar', { tipoHabitacionId: null }],
    ['tipo que ya no existe', { tipoHabitacionId: 999 }],
  ])('rechaza %s antes de enviar', (_nombre, cambios) => {
    iniciar();
    component.nuevo();
    datosValidos();
    component.formulario.patchValue(cambios);
    component.guardar();
    expect(service.crear).not.toHaveBeenCalled();
    expect(component.formulario.invalid).toBe(true);
  });

  it('crea con planta baja, recorta el número y envía el id numérico del tipo', () => {
    iniciar();
    component.nuevo();
    datosValidos();
    service.crear.mockReturnValue(
      of({ ...habitacion, id: 3, numero: '202', piso: 0, precioNoche: 50.25 }),
    );
    component.guardar();
    expect(service.crear).toHaveBeenCalledExactlyOnceWith({
      numero: '202',
      piso: 0,
      precioNoche: 50.25,
      estado: 'DISPONIBLE',
      tipoHabitacionId: 2,
    });
    expect(component.habitaciones().map((h) => h.numero)).toEqual(['101', '202']);
    expect(component.mensajeExito()).toContain('creada');
    expect(abierto()).toBeNull();
  });

  it('consulta el registro actual y permite cambiar el tipo y el estado al editar', () => {
    tiposService.listar.mockReturnValue(of([tipo, { ...tipo, id: 4, nombre: 'Suite' }]));
    service.buscar.mockReturnValue(of({ ...habitacion, precioNoche: 60 }));
    iniciar();
    component.editar(habitacion);
    expect(service.buscar).toHaveBeenCalledExactlyOnceWith(1);
    expect(component.formulario.controls.precioNoche.value).toBe(60);
    component.formulario.patchValue({ tipoHabitacionId: 4, estado: 'MANTENIMIENTO' });
    service.actualizar.mockReturnValue(
      of({
        ...habitacion,
        precioNoche: 60,
        tipoHabitacionId: 4,
        tipoHabitacionNombre: 'Suite',
        estado: 'MANTENIMIENTO',
      }),
    );
    component.guardar();
    expect(service.actualizar).toHaveBeenCalledExactlyOnceWith(1, {
      numero: '101',
      piso: 1,
      precioNoche: 60,
      tipoHabitacionId: 4,
      estado: 'MANTENIMIENTO',
    });
    expect(component.habitaciones()[0].tipoHabitacionNombre).toBe('Suite');
    expect(service.crear).not.toHaveBeenCalled();
  });

  it('conserva el formulario y la fila ante un número duplicado', () => {
    iniciar();
    component.nuevo();
    datosValidos();
    service.crear.mockReturnValue(
      throwError(() => new Error('Ya existe una habitación con ese número')),
    );
    component.guardar();
    fixture.detectChanges();
    expect(component.errorFormulario()).toContain('Ya existe');
    expect(component.formulario.controls.numero.value).toBe('202');
    expect(component.habitaciones()).toHaveLength(1);
    expect(abierto()).toBeTruthy();
    expect(component.guardando()).toBe(false);
  });

  it('explica la falta de tipos y bloquea guardar', () => {
    tiposService.listar.mockReturnValue(of([]));
    iniciar();
    component.nuevo();
    datosValidos();
    component.guardar();
    fixture.detectChanges();
    expect(component.tiposDisponibles()).toBe(false);
    expect(service.crear).not.toHaveBeenCalled();
    expect((fixture.nativeElement as HTMLElement).querySelector('a')?.getAttribute('href')).toBe(
      '/tipos-habitacion',
    );
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>(
        'button[type="submit"]',
      )?.disabled,
    ).toBe(true);
  });

  it('permite reintentar la carga de tipos sin perder lo escrito', () => {
    tiposService.listar.mockReturnValue(
      throwError(() => new Error('No se pudo acceder al servidor')),
    );
    iniciar();
    component.nuevo();
    datosValidos();
    expect(component.errorTipos()).toContain('servidor');
    expect(component.tiposDisponibles()).toBe(false);
    tiposService.listar.mockReturnValue(of([tipo]));
    component.cargarTipos();
    expect(component.errorTipos()).toBeNull();
    expect(component.tiposDisponibles()).toBe(true);
    expect(component.formulario.controls.numero.value).toBe('  202  ');
  });

  it('bloquea guardar mientras los tipos están cargando', () => {
    iniciar();
    const pendiente = new Subject<TipoHabitacionResponse[]>();
    tiposService.listar.mockReturnValue(pendiente);
    component.nuevo();
    datosValidos();
    component.guardar();
    expect(service.crear).not.toHaveBeenCalled();
    pendiente.next([tipo]);
    pendiente.complete();
    component.guardar();
    expect(service.crear).toHaveBeenCalledTimes(1);
  });

  it('no elimina al cancelar y solo envía DELETE tras confirmar', () => {
    iniciar();
    component.solicitarEliminar(habitacion);
    component.cancelarEliminacion();
    expect(service.eliminar).not.toHaveBeenCalled();
    component.solicitarEliminar(habitacion);
    component.confirmarEliminar();
    expect(service.eliminar).toHaveBeenCalledExactlyOnceWith(1);
    expect(component.habitaciones()).toEqual([]);
    expect(component.habitacionAEliminar()).toBeNull();
  });

  it('conserva la habitación y muestra la regla del servidor si tiene reservas', () => {
    iniciar();
    component.solicitarEliminar(habitacion);
    service.eliminar.mockReturnValue(
      throwError(() => new Error('La habitación tiene reservas asociadas')),
    );
    component.confirmarEliminar();
    fixture.detectChanges();
    expect(component.habitaciones()).toHaveLength(1);
    expect(component.errorEliminacion()).toContain('reservas asociadas');
    expect(abierto()).toBeTruthy();
    expect(component.eliminando()).toBe(false);
  });

  it('evita envíos duplicados y cierre durante el guardado', () => {
    iniciar();
    component.nuevo();
    datosValidos();
    const pendiente = new Subject<HabitacionResponse>();
    service.crear.mockReturnValue(pendiente);
    component.guardar();
    component.guardar();
    component.cerrarFormulario();
    expect(service.crear).toHaveBeenCalledTimes(1);
    expect(component.guardando()).toBe(true);
    expect(abierto()).toBeTruthy();
    pendiente.next({ ...habitacion, id: 3 });
    pendiente.complete();
    expect(component.guardando()).toBe(false);
    expect(abierto()).toBeNull();
  });

  it('reintenta el listado sin perder los registros ya cargados', () => {
    iniciar();
    service.listar.mockReturnValue(throwError(() => new Error('Sin conexión')));
    component.cargarHabitaciones();
    expect(component.habitaciones()).toEqual([habitacion]);
    expect(component.errorListado()).toBe('Sin conexión');
    service.listar.mockReturnValue(of([]));
    component.cargarHabitaciones();
    expect(component.errorListado()).toBeNull();
    expect(component.habitaciones()).toEqual([]);
  });

  it('informa un registro eliminado por otra persona sin abrir el editor', () => {
    iniciar();
    service.buscar.mockReturnValue(throwError(() => new Error('Habitación no existe')));
    component.editar(habitacion);
    expect(component.errorListado()).toBe('Habitación no existe');
    expect(component.cargandoEdicion()).toBeNull();
    expect(abierto()).toBeNull();
  });
});
