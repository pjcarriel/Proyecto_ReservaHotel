import { ChangeDetectionStrategy, Component } from '@angular/core';
import { PageHeaderComponent } from '../../layout/page-header/page-header';
import { EnConstruccionComponent } from '../../layout/en-construccion/en-construccion';
import { IconComponent } from '../../layout/icon/icon';

@Component({
  imports: [PageHeaderComponent, EnConstruccionComponent, IconComponent],
  selector: 'app-huespedes-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './huespedes-component.css',
  templateUrl: './huespedes-component.html',
})
export class HuespedesComponent {}
