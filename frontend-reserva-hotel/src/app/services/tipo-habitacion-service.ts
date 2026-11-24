import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActionResponse } from '../models/action-response';
import { TipoHabitacionRequest, TipoHabitacionResponse } from '../models/tipo-habitacion';

@Service()
export class TipoHabitacionService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiURL}/tipo-habitacion`;

  listar(): Observable<TipoHabitacionResponse[]> {
    return this.http
      .get<ActionResponse<TipoHabitacionResponse[]>>(this.url)
      .pipe(map((res) => res.result));
  }

  buscar(id: number): Observable<TipoHabitacionResponse> {
    return this.http
      .get<ActionResponse<TipoHabitacionResponse>>(`${this.url}/${id}`)
      .pipe(map((res) => res.result));
  }

  crear(datos: TipoHabitacionRequest): Observable<TipoHabitacionResponse> {
    return this.http
      .post<ActionResponse<TipoHabitacionResponse>>(this.url, datos)
      .pipe(map((res) => res.result));
  }

  actualizar(id: number, datos: TipoHabitacionRequest): Observable<TipoHabitacionResponse> {
    return this.http
      .put<ActionResponse<TipoHabitacionResponse>>(`${this.url}/${id}`, datos)
      .pipe(map((res) => res.result));
  }

  eliminar(id: number): Observable<null> {
    return this.http
      .delete<ActionResponse<null>>(`${this.url}/${id}`)
      .pipe(map((res) => res.result));
  }
}
