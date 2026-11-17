import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActionResponse } from '../models/action-response';

// Comprueba que el backend está levantado (GET /api/v1/health)
@Injectable({ providedIn: 'root' })
export class HealthService {
  private http = inject(HttpClient);
  private url = `${environment.apiURL}/health`;

  verificar(): Observable<ActionResponse<string>> {
    return this.http.get<ActionResponse<string>>(this.url);
  }
}
