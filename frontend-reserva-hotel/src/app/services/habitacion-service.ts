import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActionResponse } from '../models/action-response';
import { Habitacion } from '../models/habitacion';

@Injectable({ providedIn: 'root' })
export class HabitacionService {
  private http = inject(HttpClient);
  private url = `${environment.apiURL}/habitacion`;

  listar(): Observable<Habitacion[]> {
    return this.http
      .get<ActionResponse<Habitacion[]>>(this.url)
      .pipe(map(res => res.result));
  }

  buscarPorId(id: number): Observable<Habitacion> {
    return this.http
      .get<ActionResponse<Habitacion>>(`${this.url}/${id}`)
      .pipe(map(res => res.result));
  }
}