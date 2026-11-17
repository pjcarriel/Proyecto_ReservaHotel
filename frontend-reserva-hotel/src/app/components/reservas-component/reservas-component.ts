import { ChangeDetectionStrategy, Component } from '@angular/core';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { EnConstruccionComponent } from '../../layout/en-construccion/en-construccion';
import { IconComponent } from '../../layout/icon/icon';

@Component({
  imports: [PageHeaderComponent, EnConstruccionComponent, IconComponent],
  selector: 'app-reservas-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './reservas-component.css',
  templateUrl: './reservas-component.html',
})
export class ReservasComponent {}
