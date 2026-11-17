// Formato común de respuesta del backend (ActionResponse en Spring)
export interface ActionResponse<T> {
  message: string;
  result: T;
}
