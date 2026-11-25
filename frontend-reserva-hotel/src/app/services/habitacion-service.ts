import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActionResponse } from '../models/action-response';
import { HabitacionRequest, HabitacionResponse } from '../models/habitacion';

@Service()
export class HabitacionService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiURL}/habitacion`;

  listar(): Observable<HabitacionResponse[]> {
    return this.http
      .get<ActionResponse<HabitacionResponse[]>>(this.url)
      .pipe(map((res) => res.result));
  }

  buscar(id: number): Observable<HabitacionResponse> {
    return this.http
      .get<ActionResponse<HabitacionResponse>>(`${this.url}/${id}`)
      .pipe(map((res) => res.result));
  }

  crear(datos: HabitacionRequest): Observable<HabitacionResponse> {
    return this.http
      .post<ActionResponse<HabitacionResponse>>(this.url, datos)
      .pipe(map((res) => res.result));
  }

  actualizar(id: number, datos: HabitacionRequest): Observable<HabitacionResponse> {
    return this.http
      .put<ActionResponse<HabitacionResponse>>(`${this.url}/${id}`, datos)
      .pipe(map((res) => res.result));
  }

  eliminar(id: number): Observable<null> {
    return this.http
      .delete<ActionResponse<null>>(`${this.url}/${id}`)
      .pipe(map((res) => res.result));
  }
}
