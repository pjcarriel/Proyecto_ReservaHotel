import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { LoginService } from '../services/login-service';

// Sin sesión válida, manda a la pantalla de login
export const authGuard: CanActivateFn = (route, state) => {
  const loginService = inject(LoginService);
  return loginService.isAuthenticated() ? true : inject(Router).createUrlTree(['/login']);
};
