import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { LoginService } from '../services/login-service';
import { Rol } from '../models/rol';

// Solo deja entrar si el rol del usuario está en data.roles de la ruta
export const roleGuard: CanActivateFn = (route, state) => {
  const roles = (route.data['roles'] ?? []) as Rol[];
  return inject(LoginService).isRole(roles) ? true : inject(Router).createUrlTree(['/']);
};
