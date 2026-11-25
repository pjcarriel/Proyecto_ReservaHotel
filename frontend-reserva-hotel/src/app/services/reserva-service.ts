import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActionResponse } from '../models/action-response';
import { Reserva, ReservaRequest } from '../models/reserva';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private http = inject(HttpClient);
  private url = `${environment.apiURL}/reserva`;

  listar(): Observable<Reserva[]> {
    return this.http
      .get<ActionResponse<Reserva[]>>(this.url)
      .pipe(map(res => res.result));
  }

  buscarPorId(id: number): Observable<Reserva> {
    return this.http
      .get<ActionResponse<Reserva>>(`${this.url}/${id}`)
      .pipe(map(res => res.result));
  }

  crear(datos: ReservaRequest): Observable<ActionResponse<Reserva>> {
    return this.http.post<ActionResponse<Reserva>>(this.url, datos);
  }

  cancelar(id: number): Observable<ActionResponse<Reserva>> {
    return this.http.patch<ActionResponse<Reserva>>(`${this.url}/${id}/cancelar`, {});
  }
}