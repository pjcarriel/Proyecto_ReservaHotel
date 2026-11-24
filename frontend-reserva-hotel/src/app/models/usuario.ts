import { Rol } from './rol';

// Respuesta del backend (UsuarioResponse): nunca trae el password
export interface Usuario {
  id: number;
  username: string;
  rol: Rol;
  activo: boolean;
  creado: string;
  modificado: string;
}

// Body de POST y PUT /usuario
export interface UsuarioRequest {
  username: string;
  password: string;
  rol: Rol;
}
