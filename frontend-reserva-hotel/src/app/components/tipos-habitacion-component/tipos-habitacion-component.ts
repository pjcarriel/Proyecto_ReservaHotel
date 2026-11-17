import { ChangeDetectionStrategy, Component } from '@angular/core';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { EnConstruccionComponent } from '../../layout/en-construccion/en-construccion';
import { IconComponent } from '../../layout/icon/icon';

@Component({
  imports: [PageHeaderComponent, EnConstruccionComponent, IconComponent],
  selector: 'app-tipos-habitacion-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './tipos-habitacion-component.css',
  templateUrl: './tipos-habitacion-component.html',
})
export class TiposHabitacionComponent {}
