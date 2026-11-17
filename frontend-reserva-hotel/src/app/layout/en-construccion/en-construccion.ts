import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { IconComponent } from '../icon/icon';

// Vista previa de una pantalla que todavía no se implementa: barra de búsqueda,
// tabla con las columnas del módulo y filas de esqueleto, con un aviso encima.
// Sirve de guía de diseño: la pantalla real debe usar las mismas clases globales
// (.tarjeta, .campo, .tabla, .estado). Se elimina cuando el responsable haga su tarea.
@Component({
  selector: 'app-en-construccion',
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'tarjeta' },
  styles: `
    :host { display: block; position: relative; overflow: hidden; }
    .herramientas { display: flex; gap: 10px; padding: 16px 20px; border-bottom: 1px solid var(--borde); flex-wrap: wrap; }
    .buscar { position: relative; flex: 1; min-width: 200px; max-width: 360px; }
    .buscar app-icon { position: absolute; left: 12px; top: 10px; color: var(--texto-tenue); }
    .buscar .campo { padding-left: 38px; }
    .tabla { pointer-events: none; }
    .tabla td { padding: 17px 20px; }
    tbody tr:nth-child(3n + 1) .esqueleto { width: 70%; }
    tbody tr:nth-child(3n + 2) .esqueleto { width: 50%; }
    tbody tr:nth-child(3n) .esqueleto { width: 85%; }
    .aviso {
      position: absolute; inset: 69px 0 0; display: grid; place-items: center; padding: 24px;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.35) 0%, rgba(255, 255, 255, 0.92) 55%);
    }
    .aviso-caja {
      display: grid; justify-items: center; gap: 6px; text-align: center; max-width: 380px;
      padding: 24px 28px; border-radius: var(--radio-lg);
      background: var(--superficie); border: 1px solid var(--borde); box-shadow: var(--sombra);
    }
    .aviso-icono {
      width: 48px; height: 48px; border-radius: 14px; display: grid; place-items: center; margin-bottom: 4px;
      background: var(--laton-suave); color: var(--laton);
    }
    .aviso-icono app-icon { width: 24px; height: 24px; }
    strong { font-size: 15px; }
    p { color: var(--texto-tenue); font-size: 13px; }
    .etiqueta { margin-top: 6px; }
  `,
  template: `
    <div class="herramientas">
      <label class="buscar">
        <app-icon nombre="buscar" />
        <input class="campo" type="search" [placeholder]="buscar()" disabled aria-label="Buscar" />
      </label>
      <button type="button" class="btn btn-secundario" disabled><app-icon nombre="filtro" /> Filtrar</button>
    </div>

    <div class="tabla-contenedor" aria-hidden="true">
      <table class="tabla">
        <thead>
          <tr>
            @for (col of columnas(); track col) { <th>{{ col }}</th> }
          </tr>
        </thead>
        <tbody>
          @for (fila of filas; track fila) {
            <tr>
              @for (col of columnas(); track col) { <td><span class="esqueleto"></span></td> }
            </tr>
          }
        </tbody>
      </table>
    </div>

    <div class="aviso">
      <div class="aviso-caja">
        <span class="aviso-icono"><app-icon nombre="construccion" /></span>
        <strong>Pantalla en construcción</strong>
        <p>{{ detalle() }}</p>
        @if (responsable()) {
          <span class="etiqueta">Responsable: {{ responsable() }}</span>
        }
      </div>
    </div>
  `,
})
export class EnConstruccionComponent {
  columnas = input<string[]>([]);
  buscar = input('Buscar…');
  detalle = input('Muy pronto podrás gestionar esta información desde aquí.');
  responsable = input('');

  readonly filas = [1, 2, 3, 4, 5, 6];
}
