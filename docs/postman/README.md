# Pruebas de tipos de habitación, habitaciones y AOP

La colección `ReservaHotel_TipoHabitacion.postman_collection.json` prueba los cinco
endpoints del CRUD, la unicidad del nombre, las validaciones y los registros inexistentes.
Las respuestas siguen `ActionResponse { message, result }`.

La colección `ReservaHotel_Habitacion_AOP.postman_collection.json` prueba los cinco
endpoints de habitaciones, la relación con su tipo, los números únicos, los precios,
los pisos y el bloqueo de eliminación de tipos que tienen habitaciones. Cada colección
prepara sus propios datos y puede ejecutarse de manera independiente.

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

1. Selecciona **Import** e importa la colección de tipos o la de habitaciones.
2. En las variables de la colección, configura `baseUrl` con el servidor, por ejemplo
   `http://localhost:8080`, sin añadir `/api/v1`.
3. Ejecuta la colección completa con **Run collection**, respetando el orden de las carpetas.

La primera solicitud genera nombres únicos y guarda los identificadores automáticamente.
Las últimas solicitudes eliminan únicamente los registros creados por esa colección.
Si ejecutas solicitudes individuales, primero debes ejecutar sus solicitudes de preparación.

## Endpoints de tipos de habitación

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
es obligatoria y admite entre 15 y 200 caracteres. El formulario de Angular deberá usar
estos mismos límites. La colección verifica el rechazo de 14 caracteres y la aceptación de 15.
La capacidad es obligatoria y debe ser mayor a cero.

Los datos inválidos devuelven 400; un id inexistente devuelve 404; un nombre duplicado
devuelve 409. La base de datos también tiene una restricción UNIQUE para el nombre.

## Endpoints de habitaciones

| Método | Ruta | Resultado correcto |
| --- | --- | --- |
| GET | `/api/v1/habitacion` | 200, listado con los datos del tipo |
| GET | `/api/v1/habitacion/{id}` | 200, habitación encontrada |
| POST | `/api/v1/habitacion` | 201, habitación creada |
| PUT | `/api/v1/habitacion/{id}` | 200, habitación actualizada |
| DELETE | `/api/v1/habitacion/{id}` | 200, confirmación |

Cuerpo de POST y PUT; usa el id de un tipo existente en `tipoHabitacionId`:

```json
{
  "numero": "101",
  "piso": 0,
  "precioNoche": 45.50,
  "estado": "DISPONIBLE",
  "tipoHabitacionId": 1
}
```

El número es obligatorio, admite hasta 20 caracteres y es único sin distinguir
mayúsculas; se recortan sus espacios externos y se excluye el propio id al actualizar.
El piso es obligatorio y debe ser mayor o igual a cero; cero representa la planta baja.
El precio es obligatorio, admite hasta 8 enteros y 2 decimales y debe ser al menos 0.01.
El estado es obligatorio y admite `DISPONIBLE` o `MANTENIMIENTO`.

El id del tipo debe ser positivo y debe existir. Un tipo inexistente o una habitación
inexistente devuelve 404; un número duplicado devuelve 409. Los datos inválidos devuelven
400. La respuesta incluye `tipoHabitacionId`, `tipoHabitacionNombre` y las fechas de auditoría.

La relación JPA es `@ManyToOne` con clave foránea. El servicio impide eliminar tipos
que tengan habitaciones y devuelve 409; MySQL también protege esa relación. Después
de cambiar las habitaciones a otro tipo o eliminarlas, se puede eliminar el tipo original.

## Ejecutar la colección desde consola

Con Node.js instalado y el backend iniciado, desde la raíz del repositorio:

```powershell
npx.cmd --yes newman run docs/postman/ReservaHotel_TipoHabitacion.postman_collection.json
npx.cmd --yes newman run docs/postman/ReservaHotel_Habitacion_AOP.postman_collection.json
```

Para usar otro servidor:

```powershell
npx.cmd --yes newman run docs/postman/ReservaHotel_TipoHabitacion.postman_collection.json --env-var baseUrl=http://localhost:8081
```

## Precio por noche y aspectos AOP

Según la tabla de entidades del plan, `precioNoche` pertenece a `Habitacion`. El modelo de
dominio implementado usa `BigDecimal`, exige un precio positivo y permite los estados
`DISPONIBLE` y `MANTENIMIENTO`. También incluye número, piso, tipo y fechas de auditoría.
El piso debe ser mayor o igual a cero: el cero representa la planta baja. Esta validación
se aplica tanto al crear como al actualizar una habitación.

`TimeAspect` mide todos los métodos públicos de `TipoHabitacionService`, incluso si
terminan con una excepción. La duración se registra en milisegundos en el log del backend.

`AuditoriaAspect` registra únicamente `crear`, `actualizar` y `eliminar` de
`HabitacionService`: usuario, operación, id y estado `EXITO` o `ERROR`. Las consultas
no se registran como modificaciones. El éxito se registra después de que termine la
transacción; los errores conservan su excepción original. Mientras no se use una sesión
autenticada, el usuario del registro es `anonimo`. La fecha la añade el formato del log.

Ejemplos de los mensajes que aparecerán en la consola del backend:

```text
Tiempo TipoHabitacionService.listar: 3 ms
Auditoria habitacion: usuario=anonimo operacion=crear habitacionId=7 estado=EXITO
```

La dependencia `spring-boot-starter-aspectj` activa el soporte AOP. Las pruebas de los
aspectos comprueban el alcance de los métodos, el resultado devuelto y la propagación de errores.

## Verificación realizada

El 22 de noviembre de 2026 se verificó `mvnw.cmd package` con 43 pruebas Java
sin errores, incluidas pruebas de persistencia y de clave foránea en MySQL.

Las colecciones se ejecutaron con Newman contra una base temporal independiente:

| Colección | Solicitudes | Comprobaciones | Fallos |
| --- | --- | --- | --- |
| TipoHabitacion | 26 | 70 | 0 |
| Habitacion | 37 | 95 | 0 |
| Total | 63 | 165 | 0 |

Se contrastaron también los logs reales con las peticiones HTTP: 5 auditorías exitosas,
7 auditorías de error y 21 mediciones de tiempo. Las colecciones eliminaron sus datos
y se retiró la base temporal de pruebas.
