import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { filter, map } from 'rxjs';
import { IconComponent } from '../icon/icon';
import { HealthService } from '../../services/health-service';
import { SECCIONES_MENU } from './menu-opciones';

type EstadoApi = 'verificando' | 'conectada' | 'sin-conexion';

const CLAVE_COLAPSADO = 'hotel.menu-colapsado';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive, IconComponent],
  selector: 'app-menu-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './menu-component.css',
  templateUrl: './menu-component.html',
})
export class MenuComponent {
  private healthService = inject(HealthService);
  private router = inject(Router);

  // TODO (tarea #27): filtrar por el rol del usuario con loginService.isRole(op.roles)
  readonly secciones = SECCIONES_MENU;

  menuAbierto = signal(false);                       // cajón en celular
  colapsado = signal(MenuComponent.leerColapsado()); // solo íconos en escritorio
  estadoApi = signal<EstadoApi>('verificando');

  readonly fecha = new Intl.DateTimeFormat('es-EC', {
    weekday: 'long', day: 'numeric', month: 'long',
  }).format(new Date());

  private url = toSignal(
    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd),
      map(e => e.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );

  // Sección y opción de la pantalla actual, para la ruta de navegación de la barra superior
  actual = computed(() => {
    const ruta = this.url().split(/[?#]/)[0] || '/';
    for (const seccion of this.secciones) {
      const opcion = seccion.opciones.find(o => o.ruta === ruta);
      if (opcion) return { seccion: seccion.titulo, opcion };
    }
    return null;
  });

  constructor() {
    this.verificarApi();

    // En celular, cerrar el menú al cambiar de pantalla
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd), takeUntilDestroyed())
      .subscribe(() => this.cerrarMenu());

    // Recordar si el menú quedó contraído
    effect(() => {
      try {
        localStorage.setItem(CLAVE_COLAPSADO, String(this.colapsado()));
      } catch {
        // sin acceso a localStorage: no pasa nada, solo no se recuerda
      }
    });
  }

  verificarApi(): void {
    this.estadoApi.set('verificando');
    this.healthService.verificar().subscribe({
      next: () => this.estadoApi.set('conectada'),
      error: () => this.estadoApi.set('sin-conexion'),
    });
  }

  alternarMenu(): void {
    this.menuAbierto.update(v => !v);
  }

  alternarColapsado(): void {
    this.colapsado.update(v => !v);
  }

  cerrarMenu(): void {
    this.menuAbierto.set(false);
  }

  private static leerColapsado(): boolean {
    try {
      return localStorage.getItem(CLAVE_COLAPSADO) === 'true';
    } catch {
      return false;
    }
  }
}
