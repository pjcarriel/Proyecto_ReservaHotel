import { inject, Service, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';
import { LoginRequest, LoginResponse } from '../models/login-request';
import { ActionResponse } from '../models/action-response';
import { environment } from '../../environments/environment';
import { Rol } from '../models/rol';
import { JwtPayload } from '../models/jwt-payload';

@Service()
export class LoginService {
  private httpClient = inject(HttpClient);
  readonly usuarioSession = signal<LoginResponse | null>(this.checkSesion());

  login(datos: LoginRequest): Observable<LoginResponse> {
    return this.httpClient
      .post<ActionResponse<LoginResponse>>(`${environment.apiURL}/auth/login`, datos)
      .pipe(
        map(res => res.result),
        tap(sesion => {
          localStorage.setItem('sesion', JSON.stringify(sesion));
          this.usuarioSession.set(sesion);
        }));
  }

  logout(): void {
    localStorage.removeItem('sesion');
    this.usuarioSession.set(null);
  }

  getToken(): string | null {
    return this.usuarioSession()?.token ?? null;
  }

  isRole(roles: Rol[]): boolean {
    const rol = this.usuarioSession()?.rol;
    return !!rol && roles.includes(rol);
  }

  // ¿Hay sesión iniciada y el token sigue siendo válido?
  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && !this.isExpired(token);
  }

  private checkSesion(): LoginResponse | null {
    try {
      const usuario = localStorage.getItem('sesion');
      return usuario ? JSON.parse(usuario) : null;
    } catch {
      return null;
    }
  }

  private isExpired(token: string): boolean {
    try {
      const payload: JwtPayload = JSON.parse(atob(token.split('.')[1]));
      return Date.now() >= payload.exp * 1000;
    } catch {
      return true;
    }
  }
}
