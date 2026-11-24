import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActionResponse } from '../models/action-response';
import { Usuario, UsuarioRequest } from '../models/usuario';

@Service()
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiURL}/usuario`;

  listar(): Observable<Usuario[]> {
    return this.http
      .get<ActionResponse<Usuario[]>>(this.url)
      .pipe(map(res => res.result));
  }

  buscar(id: number): Observable<Usuario> {
    return this.http
      .get<ActionResponse<Usuario>>(`${this.url}/${id}`)
      .pipe(map(res => res.result));
  }

  crear(datos: UsuarioRequest): Observable<Usuario> {
    return this.http
      .post<ActionResponse<Usuario>>(this.url, datos)
      .pipe(map(res => res.result));
  }

  actualizar(id: number, datos: UsuarioRequest): Observable<Usuario> {
    return this.http
      .put<ActionResponse<Usuario>>(`${this.url}/${id}`, datos)
      .pipe(map(res => res.result));
  }

  // PATCH /usuario/{id} con body {"activo": true|false}
  cambiarEstado(id: number, activo: boolean): Observable<Usuario> {
    return this.http
      .patch<ActionResponse<Usuario>>(`${this.url}/${id}`, { activo })
      .pipe(map(res => res.result));
  }
}
