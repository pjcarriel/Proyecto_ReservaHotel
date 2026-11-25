# FrontendReservaHotel

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 22.2.7.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.

## Pantalla de tipos de habitación — actividad #19

Ruta: `/tipos-habitacion`. La pantalla utiliza el diseño común del proyecto y el
acceso protegido por los guards e interceptores de la base de autenticación.

Desde la raíz del repositorio:

```powershell
cd frontend-reserva-hotel
npm.cmd ci
npm.cmd start
```

El backend se configura mediante `environment.apiURL`, actualmente
`http://localhost:8080/api/v1`. Para entrar con credenciales reales debe estar
disponible el endpoint de autenticación `/auth/login`, además de `/tipo-habitacion`.

La pantalla incluye listado y búsqueda por nombre o descripción, consulta por id
antes de editar, formulario reactivo para crear y actualizar, y confirmación antes
de enviar DELETE. Los mensajes del servidor se muestran conservando el formulario
o la fila cuando una operación falla, incluidos los conflictos de nombres y tipos
que tienen habitaciones asociadas. Las acciones pendientes bloquean envíos duplicados.

Validaciones del formulario, de acuerdo con el backend:

- Nombre obligatorio, hasta 60 caracteres y sin espacios externos al guardar.
- Descripción obligatoria, entre 15 y 200 caracteres.
- Capacidad entera mayor que cero.

Modelo: `src/app/models/tipo-habitacion.ts`, con interfaces de petición y respuesta.
Servicio: `TipoHabitacionService`, con listar, buscar, crear, actualizar y eliminar.
Las respuestas usan `ActionResponse` y el modelo conserva `creado` y `modificado`.

Verificación del 23 de noviembre de 2026:

```powershell
npm.cmd run build
npm.cmd test -- --watch=false
```

La compilación de producción y las 23 pruebas Angular pasaron. Se verificaron
19 comprobaciones adicionales en Chrome, incluyendo escritorio, móvil, teclado,
errores, confirmaciones y el CRUD contra una base MySQL temporal independiente.
El acceso en esos recorridos se preparó con una sesión de prueba del frontend;
la comprobación se centró en el CRUD de tipos de habitación.

## Pantalla de habitaciones — actividades #22 y #25

Ruta: `/habitaciones`. Requiere iniciar sesión y utiliza `environment.apiURL`,
`HabitacionService` y el catálogo de `TipoHabitacionService`. La tabla muestra
número, tipo, piso, precio por noche en USD y estado. Se puede combinar la búsqueda
por número o tipo con los filtros Todas, Disponibles y Mantenimiento.

El formulario reactivo permite crear y editar. Consulta el registro por id antes
de editar y actualiza los tipos al abrirse, conserva lo escrito ante errores y
evita envíos duplicados. Eliminar requiere confirmación. Los diálogos admiten
teclado y la pantalla se adapta al móvil; la tabla permite desplazamiento horizontal.
Las dos entradas del menú, Tipos y Habitaciones, tienen `listo: true`.

Validaciones alineadas con `HabitacionRequest`:

- Número o código obligatorio, hasta 20 caracteres, con espacios externos recortados.
  El backend comprueba que sea único.
- Piso entero entre 0 y 2,147,483,647. El piso 0 es planta baja.
- Precio por noche entre 0.01 y 99,999,999.99, con hasta dos decimales.
- Estado `DISPONIBLE` o `MANTENIMIENTO`.
- Tipo obligatorio seleccionado del catálogo. Si los tipos fallan al cargar,
  se ofrece reintentar; si no existen tipos, se ofrece ir a crearlos. Guardar
  permanece bloqueado hasta que el catálogo esté disponible.

`DISPONIBLE` describe el estado operativo de la habitación. El backend de reservas
comprueba la disponibilidad para las fechas solicitadas; una habitación puede
estar operativa y tener reservas en determinados días.

### Verificación del 25 de noviembre de 2026

`main` y `feature/stalin-front` recibieron la base `b31f6cd`, que ya contiene
el CRUD backend de Habitaciones (#48), la pantalla de Tipos (#50), el login y
las pantallas de Huéspedes y listado/cancelación de Reservas.

```powershell
npm.cmd run build
npm.cmd test -- --watch=false
```

La compilación de producción y las 64 pruebas Angular pasaron, incluidas 32
pruebas nuevas del servicio y la pantalla de Habitaciones. Se comprobaron 16
escenarios adicionales en Chrome: login real con JWT, CRUD persistido en MySQL,
validaciones, duplicados, filtros, errores y reintentos, confirmación, teclado,
escritorio y móvil de 390 px, e integración con reservas. Se usaron datos ficticios
en una base temporal independiente; no se modificaron los datos habituales del hotel.

### Comprobaciones para el formulario de reservas de Jonathan

La versión sincronizada todavía no contiene el formulario de creación: el botón
«Nueva reserva» está deshabilitado. Queda pendiente probar ese formulario cuando
Jonathan lo publique. Sí se verificaron su listado, detalle y cancelación con
reservas reales creadas mediante `POST /api/v1/reserva`.

Para repetir la integración, inicia sesión, registra un huésped de prueba y crea
una habitación disponible desde `/habitaciones`. Con los ids obtenidos, la petición
de creación de reserva tiene este contrato (utiliza fechas futuras e ids existentes):

```json
{
  "huespedId": 1,
  "fechaEntrada": "2026-12-10",
  "fechaSalida": "2026-12-13",
  "habitacionIds": [1]
}
```

Envía el token del login como `Authorization: Bearer <token>`. Para tres noches
con una tarifa de 60.50, el backend probado devuelve subtotal 181.50, IVA del proyecto
27.23 y total 208.73. La reserva debe aparecer en `/reservas` con el tipo, número,
tarifa y noches correctos en su detalle.

Casos comprobados para repetir al disponer del formulario:

- Reserva válida: HTTP 201 y totales calculados con el precio de la habitación.
- Fechas cruzadas para la misma habitación: HTTP 409. Fechas contiguas son válidas.
- Habitación en mantenimiento: HTTP 409.
- Salida igual a la entrada o sin habitaciones: HTTP 400.
- Huésped inexistente: HTTP 404.
- Cancelar desde la pantalla cambia el estado a `CANCELADA` y permite reservar
  de nuevo las mismas fechas. La cancelación conserva el historial.
