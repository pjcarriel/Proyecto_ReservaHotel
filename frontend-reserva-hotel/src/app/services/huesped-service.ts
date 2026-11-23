import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActionResponse } from '../models/action-response';
import { Huesped, HuespedRequest } from '../models/huesped';

@Injectable({ providedIn: 'root' })
export class HuespedService {
  private http = inject(HttpClient);
  private url = `${environment.apiURL}/huesped`;

  listar(): Observable<Huesped[]> {
    return this.http
      .get<ActionResponse<Huesped[]>>(this.url)
      .pipe(map(res => res.result));
  }

  buscarPorId(id: number): Observable<Huesped> {
    return this.http
      .get<ActionResponse<Huesped>>(`${this.url}/${id}`)
      .pipe(map(res => res.result));
  }

  crear(datos: HuespedRequest): Observable<ActionResponse<Huesped>> {
    return this.http.post<ActionResponse<Huesped>>(this.url, datos);
  }

  actualizar(id: number, datos: HuespedRequest): Observable<ActionResponse<Huesped>> {
    return this.http.put<ActionResponse<Huesped>>(`${this.url}/${id}`, datos);
  }

  eliminar(id: number): Observable<ActionResponse<void>> {
    return this.http.delete<ActionResponse<void>>(`${this.url}/${id}`);
  }
}