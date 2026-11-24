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
