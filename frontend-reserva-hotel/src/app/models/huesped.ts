export interface Huesped {
  id: number;
  cedula: string;
  nombres: string;
  apellidos: string;
  email: string;
  telefono: string | null;
  creado: string;
  modificado: string;
}

export interface HuespedRequest {
  cedula: string;
  nombres: string;
  apellidos: string;
  email: string;
  telefono: string | null;
}