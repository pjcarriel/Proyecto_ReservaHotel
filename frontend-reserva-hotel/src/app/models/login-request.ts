import { Rol } from './rol';

export interface LoginRequest {
  username: string;
  password: string;
}

// Respuesta de POST /auth/login (dentro de ActionResponse.result)
export interface LoginResponse {
  token: string;
  username: string;
  nombre: string;
  rol: Rol;
  expiraEn: number; // milisegundos de validez del token
}
