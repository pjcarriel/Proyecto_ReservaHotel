import { Rol } from './rol';

// Datos que el backend (JWTServices.generar) guarda dentro del token
export interface JwtPayload {
  sub: string;  // username
  rol: Rol;
  iat: number;  // fecha de emisión, en segundos
  exp: number;  // fecha de expiración, en segundos
}
