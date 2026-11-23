import { ChangeDetectionStrategy, Component, input } from '@angular/core';

// Íconos de línea (24x24) usados en toda la app: <app-icon nombre="reservas" />
@Component({
  selector: 'app-icon',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { 'aria-hidden': 'true' },
  styles: `
    :host { display: inline-grid; place-items: center; flex-shrink: 0; width: 18px; height: 18px; }
    svg { width: 100%; height: 100%; fill: none; stroke: currentColor; stroke-width: 1.7;
          stroke-linecap: round; stroke-linejoin: round; }
  `,
  template: `
    <svg viewBox="0 0 24 24">
      @switch (nombre()) {
        @case ('inicio') { <path d="M3 10.5 12 3l9 7.5M5.5 9v11.5h13V9M10 20.5v-6h4v6" /> }
        @case ('reservas') { <rect x="3.5" y="5" width="17" height="15.5" rx="2" /><path d="M3.5 10h17M8 3v4M16 3v4M8.5 14.5l2 2 4-4" /> }
        @case ('huespedes') { <circle cx="9" cy="8" r="3.5" /><path d="M2.5 20a6.5 6.5 0 0 1 13 0M16 4.6a3.5 3.5 0 0 1 0 6.8M21.5 20a6.5 6.5 0 0 0-3.5-5.8" /> }
        @case ('habitaciones') { <path d="M3 6v14M3 16h18M21 20v-6a3 3 0 0 0-3-3h-7.5v5" /><circle cx="6.8" cy="12.2" r="1.9" /> }
        @case ('tipos') { <path d="m12 3 9 4.5-9 4.5-9-4.5z" /><path d="m3 12 9 4.5 9-4.5M3 16.5 12 21l9-4.5" /> }
        @case ('usuarios') { <path d="M12 3 4.5 6v5.5c0 4.4 3.1 8 7.5 9.5 4.4-1.5 7.5-5.1 7.5-9.5V6z" /><circle cx="12" cy="10" r="2.3" /><path d="M8.5 16a3.6 3.6 0 0 1 7 0" /> }
        @case ('campana') { <path d="M3 18h18M5 18a7 7 0 0 1 14 0M12 11V8M10 8h4" /> }
        @case ('menu') { <path d="M4 7h16M4 12h16M4 17h16" /> }
        @case ('calendario') { <rect x="3.5" y="5" width="17" height="15.5" rx="2" /><path d="M3.5 10h17M8 3v4M16 3v4" /> }
        @case ('cama-libre') { <path d="M3 6v14M3 16h18M21 20v-6a3 3 0 0 0-3-3h-7.5v5" /><path d="m14 5 1.5 1.5L18.5 3.5" /> }
        @case ('flecha') { <path d="M5 12h14M13 6l6 6-6 6" /> }
        @case ('construccion') { <path d="M3 20h18M5 20 9.5 6h5L19 20M7.7 13h8.6M8.8 9.5h6.4" /> }
        @case ('mas') { <path d="M12 5v14M5 12h14" /> }
        @case ('buscar') { <circle cx="11" cy="11" r="6.5" /><path d="m20 20-4.2-4.2" /> }
        @case ('filtro') { <path d="M4 6h16M7 12h10M10 18h4" /> }
        @case ('panel') { <rect x="3.5" y="4.5" width="17" height="15" rx="2.5" /><path d="M9.5 4.5v15" /> }
        @case ('chevron') { <path d="m9 6 6 6-6 6" /> }
        @case ('llegada') { <path d="M14 4h4a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-4M4 12h11M11 8l4 4-4 4" /> }
        @case ('salida') { <path d="M10 4H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h4M9 12h11M16 8l4 4-4 4" /> }
        @case ('huesped-mas') { <circle cx="9" cy="8" r="3.5" /><path d="M2.5 20a6.5 6.5 0 0 1 13 0M19 8v6M16 11h6" /> }
        @case ('usuario') { <circle cx="12" cy="8" r="4" /><path d="M4.5 20.5a7.5 7.5 0 0 1 15 0" /> }
        @case ('candado') { <rect x="4.5" y="10.5" width="15" height="10" rx="2" /><path d="M8 10.5V7.5a4 4 0 0 1 8 0v3M12 14.5v2.5" /> }
        @case ('ojo') { <path d="M2.5 12S6 5.5 12 5.5 21.5 12 21.5 12 18 18.5 12 18.5 2.5 12 2.5 12z" /><circle cx="12" cy="12" r="3" /> }
        @case ('ojo-cerrado') { <path d="M3 3l18 18M10.6 5.6A9.9 9.9 0 0 1 12 5.5c6 0 9.5 6.5 9.5 6.5a17 17 0 0 1-3 3.8M6.3 6.4A16.6 16.6 0 0 0 2.5 12S6 18.5 12 18.5a9.4 9.4 0 0 0 4.2-1M9.9 9.9a3 3 0 0 0 4.2 4.2" /> }
        @case ('alerta') { <circle cx="12" cy="12" r="9" /><path d="M12 7.5v5.5M12 16.5h.01" /> }
      }
    </svg>
  `,
})
export class IconComponent {
  nombre = input.required<string>();
}
