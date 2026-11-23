import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LoginService } from '../services/login-service';
import { environment } from '../../environments/environment';

// Agrega "Authorization: Bearer <token>" a las peticiones al backend (menos al login)
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(LoginService).getToken();

  const isSource = req.url.startsWith(environment.apiURL);
  const isLogin = req.url.endsWith('/auth/login');

  if (token && isSource && !isLogin) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
