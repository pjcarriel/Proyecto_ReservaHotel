import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';
import { TipoHabitacionService } from '../../services/tipo-habitacion-service';
import { TipoHabitacionRequest, TipoHabitacionResponse } from '../../models/tipo-habitacion';
import { TiposHabitacionComponent } from './tipos-habitacion-component';

const tipo: TipoHabitacionResponse = {
  id: 1,
  nombre: 'Doble',
  descripcion: 'Habitación confortable para dos personas',
  capacidad: 2,
  creado: '2026-11-23T09:00:00',
  modificado: '2026-11-23T09:00:00',
};

function servicioSimulado() {
  return {
    listar: vi.fn(() => of([tipo])),
    buscar: vi.fn((_id: number) => of(tipo)),
    crear: vi.fn((_datos: TipoHabitacionRequest) => of({ ...tipo, id: 2 })),
    actualizar: vi.fn((_id: number, _datos: TipoHabitacionRequest) => of(tipo)),
    eliminar: vi.fn((_id: number) => of(null)),
  };
}

describe('TiposHabitacionComponent', () => {
  let fixture: ComponentFixture<TiposHabitacionComponent>;
  let component: TiposHabitacionComponent;
  let service: ReturnType<typeof servicioSimulado>;

  beforeEach(async () => {
    service = servicioSimulado();
    await TestBed.configureTestingModule({
      imports: [TiposHabitacionComponent],
      providers: [{ provide: TipoHabitacionService, useValue: service }],
    }).compileComponents();
    fixture = TestBed.createComponent(TiposHabitacionComponent);
    component = fixture.componentInstance;
  });

  function iniciar(): void {
    fixture.detectChanges();
    // jsdom no implementa los métodos nativos del diálogo. Los recorridos de navegador
    // comprueban showModal/close reales; aquí solo se simula el estado de apertura.
    for (const dialog of (fixture.nativeElement as HTMLElement).querySelectorAll('dialog')) {
      dialog.showModal = () => dialog.setAttribute('open', '');
      dialog.close = () => dialog.removeAttribute('open');
    }
  }

  function datosValidos(): void {
    component.formulario.setValue({
      nombre: 'Familiar',
      descripcion: 'Descripcion uno',
      capacidad: 3,
    });
  }

  it('carga la tabla y filtra por nombre o descripción', () => {
    service.listar.mockReturnValue(
      of([tipo, { ...tipo, id: 2, nombre: 'Suite', descripcion: 'Suite con vista al jardín' }]),
    );
    iniciar();
    const buscar = (fixture.nativeElement as HTMLElement).querySelector<HTMLInputElement>(
      'input[type="search"]',
    )!;
    buscar.value = 'jardín';
    buscar.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(service.listar).toHaveBeenCalledTimes(1);
    expect(component.tiposFiltrados().map((t) => t.nombre)).toEqual(['Suite']);
    expect((fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr').length).toBe(1);
  });

  it('rechaza nombre vacío, descripción de 14 caracteres y capacidad decimal antes de enviar', () => {
    iniciar();
    component.nuevo();
    component.formulario.setValue({ nombre: '   ', descripcion: 'x'.repeat(14), capacidad: 1.5 });

    component.guardar();
    fixture.detectChanges();

    expect(service.crear).not.toHaveBeenCalled();
    expect(component.invalido('nombre')).toBe(true);
    expect(component.invalido('descripcion')).toBe(true);
    expect(component.invalido('capacidad')).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('entre 15 y 200 caracteres');
  });

  it('crea con descripción de 15 caracteres, normaliza textos y agrega la fila', () => {
    iniciar();
    component.nuevo();
    datosValidos();
    component.formulario.controls.nombre.setValue('  Familiar  ');
    service.crear.mockReturnValue(
      of({ ...tipo, id: 2, nombre: 'Familiar', descripcion: 'Descripcion uno', capacidad: 3 }),
    );

    component.guardar();
    fixture.detectChanges();

    expect(service.crear).toHaveBeenCalledWith({
      nombre: 'Familiar',
      descripcion: 'Descripcion uno',
      capacidad: 3,
    });
    expect(component.tipos().length).toBe(2);
    expect((fixture.nativeElement as HTMLElement).querySelector('dialog[open]')).toBeNull();
  });

  it('consulta el registro al editar y actualiza su fila sin crear otro tipo', () => {
    iniciar();
    component.editar(tipo);
    expect(service.buscar).toHaveBeenCalledWith(1);
    expect(component.formulario.controls.nombre.value).toBe('Doble');
    datosValidos();
    service.actualizar.mockReturnValue(
      of({ ...tipo, nombre: 'Familiar', descripcion: 'Descripcion uno', capacidad: 3 }),
    );

    component.guardar();

    expect(service.actualizar).toHaveBeenCalledWith(1, {
      nombre: 'Familiar',
      descripcion: 'Descripcion uno',
      capacidad: 3,
    });
    expect(service.crear).not.toHaveBeenCalled();
    expect(component.tipos()[0].nombre).toBe('Familiar');
    expect(component.tipos().length).toBe(1);
  });

  it('conserva el formulario y muestra el mensaje del backend si el nombre está duplicado', () => {
    iniciar();
    component.nuevo();
    datosValidos();
    service.crear.mockReturnValue(
      throwError(() => new Error('Ya existe un tipo de habitación con ese nombre')),
    );

    component.guardar();
    fixture.detectChanges();

    expect(component.formulario.controls.nombre.value).toBe('Familiar');
    expect(component.errorFormulario()).toContain('Ya existe');
    expect((fixture.nativeElement as HTMLElement).querySelector('dialog[open]')).toBeTruthy();
    expect(component.guardando()).toBe(false);
    expect(component.tipos().length).toBe(1);
  });

  it('solo elimina después de confirmar y permite cancelar sin enviar DELETE', () => {
    iniciar();
    component.solicitarEliminar(tipo);
    expect(service.eliminar).not.toHaveBeenCalled();
    component.cancelarEliminacion();
    expect(service.eliminar).not.toHaveBeenCalled();
    component.solicitarEliminar(tipo);

    component.confirmarEliminar();

    expect(service.eliminar).toHaveBeenCalledExactlyOnceWith(1);
    expect(component.tipos()).toEqual([]);
    expect(component.tipoAEliminar()).toBeNull();
  });

  it('mantiene la fila y la confirmación si el tipo tiene habitaciones asociadas', () => {
    iniciar();
    component.solicitarEliminar(tipo);
    service.eliminar.mockReturnValue(
      throwError(() => new Error('El tipo tiene habitaciones asociadas')),
    );

    component.confirmarEliminar();
    fixture.detectChanges();

    expect(component.tipos().length).toBe(1);
    expect(component.errorEliminacion()).toContain('habitaciones asociadas');
    expect(component.eliminando()).toBe(false);
    expect((fixture.nativeElement as HTMLElement).querySelector('dialog[open]')).toBeTruthy();
  });

  it('permite reintentar la carga después de un error de conexión', () => {
    service.listar.mockReturnValue(throwError(() => new Error('No se pudo acceder al servidor')));
    iniciar();
    expect(component.errorListado()).toContain('servidor');
    service.listar.mockReturnValue(of([tipo]));

    component.cargarTipos();

    expect(service.listar).toHaveBeenCalledTimes(2);
    expect(component.errorListado()).toBeNull();
    expect(component.tipos()).toEqual([tipo]);
  });

  it('evita envíos duplicados y bloquea el cierre mientras guarda', () => {
    iniciar();
    component.nuevo();
    datosValidos();
    const pendiente = new Subject<TipoHabitacionResponse>();
    service.crear.mockReturnValue(pendiente);

    component.guardar();
    component.guardar();
    component.cerrarFormulario();

    expect(service.crear).toHaveBeenCalledTimes(1);
    expect(component.guardando()).toBe(true);
    expect((fixture.nativeElement as HTMLElement).querySelector('dialog[open]')).toBeTruthy();
    pendiente.next({ ...tipo, id: 2, nombre: 'Familiar' });
    pendiente.complete();
    expect(component.guardando()).toBe(false);
    expect((fixture.nativeElement as HTMLElement).querySelector('dialog[open]')).toBeNull();
  });
});
