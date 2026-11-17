import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { IconComponent } from '../icon/icon';
import { HealthService } from '../../services/health-service';
import { SECCIONES_MENU } from './menu-opciones';

type EstadoApi = 'verificando' | 'conectada' | 'sin-conexion';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive, IconComponent],
  selector: 'app-menu-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './menu-component.css',
  templateUrl: './menu-component.html',
})
export class MenuComponent {
  private healthService = inject(HealthService);

  // TODO (tarea #27): filtrar por el rol del usuario con loginService.isRole(op.roles)
  readonly secciones = SECCIONES_MENU;

  menuAbierto = signal(false);
  estadoApi = signal<EstadoApi>('verificando');

  readonly fecha = new Intl.DateTimeFormat('es-EC', {
    weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
  }).format(new Date());

  constructor() {
    this.verificarApi();

    // En celular, cerrar el menú al cambiar de pantalla
    inject(Router).events
      .pipe(filter(e => e instanceof NavigationEnd), takeUntilDestroyed())
      .subscribe(() => this.cerrarMenu());
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

  cerrarMenu(): void {
    this.menuAbierto.set(false);
  }
}
