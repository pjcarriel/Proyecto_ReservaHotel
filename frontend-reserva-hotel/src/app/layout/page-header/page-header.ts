import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { IconComponent } from '../icon/icon';

// Encabezado común de cada pantalla. Los botones de acción se pasan como contenido:
// <app-page-header titulo="Huéspedes" descripcion="..." icono="huespedes">
//   <button class="btn btn-primario">Nuevo</button>
// </app-page-header>
@Component({
  selector: 'app-page-header',
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    :host { display: flex; align-items: flex-end; justify-content: space-between;
            gap: 16px; flex-wrap: wrap; margin-bottom: 24px; }
    .titulo { display: flex; align-items: center; gap: 14px; min-width: 0; }
    .icono { width: 44px; height: 44px; border-radius: 12px; display: grid; place-items: center;
             background: var(--laton-suave); color: var(--laton); border: 1px solid #ecdcc0; }
    .icono app-icon { width: 22px; height: 22px; }
    h1 { font-size: 22px; font-weight: 700; letter-spacing: -0.02em; line-height: 1.2; }
    p { color: var(--texto-tenue); margin-top: 2px; }
    .acciones { display: flex; gap: 8px; flex-wrap: wrap; }
  `,
  template: `
    <div class="titulo">
      <div class="icono"><app-icon [nombre]="icono()" /></div>
      <div>
        <h1>{{ titulo() }}</h1>
        @if (descripcion()) { <p>{{ descripcion() }}</p> }
      </div>
    </div>
    <div class="acciones"><ng-content /></div>
  `,
})
export class PageHeaderComponent {
  titulo = input.required<string>();
  descripcion = input('');
  icono = input('inicio');
}
