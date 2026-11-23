import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { LoginService } from '../services/login-service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

// Convierte los errores HTTP en un Error con el mensaje del backend (ActionResponse.message).
// Si el token ya no sirve (401), cierra la sesión y vuelve al login.
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const loginService = inject(LoginService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((er: HttpErrorResponse) => {
      let message: string;
      if (er.status === 0) {
        message = 'No se pudo acceder al servidor';
      } else {
        message = er.error?.message ?? 'Error no controlado';
      }
      const isLogin = req.url.endsWith('/auth/login');
      if (er.status === 401 && !isLogin) {
        loginService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => new Error(message));
    })
  );
};
