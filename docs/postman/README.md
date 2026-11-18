# Pruebas de TipoHabitacion

La colección `ReservaHotel_TipoHabitacion.postman_collection.json` prueba los cinco
endpoints del CRUD, la unicidad del nombre, las validaciones y los registros inexistentes.
Las respuestas siguen `ActionResponse { message, result }`.

## Iniciar el backend

Requisitos: Java 21 y MySQL en ejecución. Desde `Backend_ReservasHotel`, configura
tu conexión usando las variables `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD` o el archivo
ignorado `application-local.properties`, siguiendo `application-local.properties.example`.

```powershell
.\mvnw.cmd spring-boot:run
```

La configuración compartida crea la base y las tablas automáticamente. La seguridad
actual permite probar estas rutas sin token.

## Importar y ejecutar en Postman

1. Selecciona **Import** e importa `ReservaHotel_TipoHabitacion.postman_collection.json`.
2. En las variables de la colección, configura `baseUrl` con el servidor, por ejemplo
   `http://localhost:8080`, sin añadir `/api/v1`.
3. Ejecuta la colección completa con **Run collection**, respetando el orden de las carpetas.

La primera solicitud genera nombres únicos y guarda los identificadores automáticamente.
Las últimas solicitudes eliminan los dos registros creados durante la prueba. Si ejecutas
solicitudes individuales, primero debes ejecutar las dos solicitudes de creación.

## Endpoints

| Método | Ruta | Resultado correcto |
| --- | --- | --- |
| GET | `/api/v1/tipo-habitacion` | 200, listado |
| GET | `/api/v1/tipo-habitacion/{id}` | 200, tipo encontrado |
| POST | `/api/v1/tipo-habitacion` | 201, tipo creado |
| PUT | `/api/v1/tipo-habitacion/{id}` | 200, tipo actualizado |
| DELETE | `/api/v1/tipo-habitacion/{id}` | 200, confirmación |

Ejemplo del cuerpo de POST y PUT:

```json
{
  "nombre": "Doble",
  "descripcion": "Habitación confortable para dos personas",
  "capacidad": 2
}
```

El nombre es obligatorio, admite hasta 60 caracteres y se almacena sin espacios externos.
La unicidad ignora mayúsculas y excluye el propio registro al actualizar. La descripción
es obligatoria y admite entre 10 y 200 caracteres, siguiendo la referencia de Facturación.
La capacidad es obligatoria y debe ser mayor a cero.

Los datos inválidos devuelven 400; un id inexistente devuelve 404; un nombre duplicado
devuelve 409. La base de datos también tiene una restricción UNIQUE para el nombre.

## Ejecutar la colección desde consola

Con Node.js instalado y el backend iniciado, desde la raíz del repositorio:

```powershell
npx.cmd --yes newman run docs/postman/ReservaHotel_TipoHabitacion.postman_collection.json
```

Para usar otro servidor:

```powershell
npx.cmd --yes newman run docs/postman/ReservaHotel_TipoHabitacion.postman_collection.json --env-var baseUrl=http://localhost:8081
```

## Precio por noche y alcance

Según la tabla de entidades del plan, `precioNoche` pertenece a `Habitacion`. El modelo de
dominio implementado usa `BigDecimal`, exige un precio positivo y permite los estados
`DISPONIBLE` y `MANTENIMIENTO`. También incluye número, piso, tipo y fechas de auditoría.

Los cinco endpoints de esta colección corresponden a `TipoHabitacion`. La persistencia,
los puertos y el CRUD de `Habitacion`, así como la comprobación de habitaciones asociadas
antes de eliminar un tipo, corresponden a las siguientes actividades del plan. El
adaptador de tipos ya convierte las restricciones de integridad de la base en una respuesta 409.

## Verificación realizada

El 17 de noviembre de 2026 se verificó `mvnw.cmd package` con 17 pruebas de Java
sin errores. La colección se ejecutó con Newman contra MySQL: 26 solicitudes y
70 comprobaciones, todas correctas. Se usó una base temporal independiente y las
solicitudes de limpieza eliminaron los dos registros creados por la colección.
