# Lista de Compras (Android, Java)

Aplicación Android offline para gestionar una lista de compras, desarrollada en Java, usando SQLite como base de datos local.

## Características
- Registrar productos con nombre, estado (PENDIENTE/COMPRADO) y fecha (yyyy-MM-dd)
- Agregar, editar y eliminar productos
- Filtrar por estado y rango de fechas
- Reporte con conteo total y por estado
- Swipe-to-delete con Snackbar para deshacer
- Tema oscuro/noche aplicado a toda la app
- Todas las operaciones de BD en background (ExecutorService)
- Validaciones y manejo de errores con Toast/Snackbar

## Requisitos
- Android Studio (recomendado Arctic Fox o superior)
- MinSdkVersion: 21 (Android 5.0)
- Java 11

## Instalación y Ejecución
1. Clona el repositorio o copia el proyecto en tu workspace.
2. Abre el proyecto en Android Studio.
3. Sincroniza Gradle (el archivo `build.gradle.kts` ya incluye todas las dependencias necesarias).
4. Compila y ejecuta la app en un emulador o dispositivo físico con Android 5.0 o superior.

## Uso
- **Agregar producto:** Pulsa el botón flotante (+) en la pantalla principal.
- **Editar producto:** Pulsa sobre un producto en la lista.
- **Eliminar producto:** Desliza el producto hacia la izquierda o derecha. Puedes deshacer en el Snackbar.
- **Filtrar:** Usa el menú de la Toolbar para aplicar filtros por estado y fechas.
- **Reporte:** Accede al reporte desde el menú para ver conteos.
- **Ajustes:** Cambia el tema desde la pantalla de ajustes.

## Estructura del Proyecto
- `app/src/main/java/com/example/lista1_de_compras/` : Clases Java principales
- `app/src/main/res/layout/` : Layouts XML
- `app/src/main/res/values/` y `values-night/` : Estilos y temas
- `app/src/main/AndroidManifest.xml` : Configuración de actividades
- `app/build.gradle.kts` : Configuración de dependencias y compilación

## Pruebas
- Ejecuta la app y prueba todas las funcionalidades: agregar, editar, eliminar, filtrar, reporte y cambio de tema.
- Verifica el manejo de errores y validaciones (nombre vacío, fecha nula, estado inválido).

## Comentarios
Todos los archivos Java y XML incluyen comentarios explicativos para facilitar el mantenimiento y la comprensión del código.

---
Desarrollado siguiendo buenas prácticas de Android y Material Design.

