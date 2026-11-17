import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { IconComponent } from '../icon/icon';

// Aviso para las pantallas que todavía no se implementan.
// Se reemplaza por el contenido real cuando el responsable haga su tarea.
@Component({
  selector: 'app-en-construccion',
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    :host { display: grid; place-items: center; text-align: center; padding: 56px 24px; }
    .icono { width: 56px; height: 56px; border-radius: 16px; display: grid; place-items: center;
             background: var(--superficie-2); border: 1px dashed var(--borde-fuerte);
             color: var(--texto-tenue); margin-bottom: 16px; }
    .icono app-icon { width: 26px; height: 26px; }
    h2 { font-size: 16px; font-weight: 650; }
    p { color: var(--texto-tenue); max-width: 420px; margin-top: 6px; }
    .etiqueta { margin-top: 14px; }
  `,
  host: { class: 'tarjeta' },
  template: `
    <div class="icono"><app-icon nombre="construccion" /></div>
    <h2>Pantalla en construcción</h2>
    <p>{{ detalle() }}</p>
    @if (responsable()) {
      <span class="etiqueta">Responsable: {{ responsable() }}</span>
    }
  `,
})
export class EnConstruccionComponent {
  detalle = input('Muy pronto podrás gestionar esta información desde aquí.');
  responsable = input('');
}
