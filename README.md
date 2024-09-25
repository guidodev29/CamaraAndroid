# Proyecto Android: Gestión de Permisos y Pruebas de Rendimiento

## Descripción
Esta aplicación Android está diseñada para evaluar la implementación y gestión de permisos en Android 14. La aplicación permite solicitar y gestionar permisos en tiempo de ejecución, realizar acciones específicas según los permisos concedidos y realizar pruebas de rendimiento utilizando Android Profiler.

## Características Principales
- **Gestión de Permisos en Tiempo de Ejecución:**
  - Permisos solicitados: Cámara, Ubicación y Almacenamiento.
  - Muestra el estado de cada permiso (concedido o denegado).
  - Botones para solicitar cada permiso individualmente.
- **Funcionalidades Basadas en Permisos:**
  - Tomar una foto (si se concede el permiso de cámara).
  - Acceder a la ubicación (si se concede el permiso de ubicación).
  - Leer un archivo del almacenamiento externo (si se concede el permiso de almacenamiento).
- **Marca de Agua en Imágenes:**
  - Agrega una marca de agua a las fotos con la ubicación actual del usuario.
  - La marca de agua se ajusta en tamaño y posición y permite saltos de línea para evitar que sea muy larga.

## Pruebas de Rendimiento
Se realizaron pruebas para evaluar el uso de recursos de la aplicación en diferentes escenarios:
1. **Sin permisos solicitados.**
2. **Con todos los permisos concedidos.**
3. **Con uno o más permisos denegados.**

### Resultados de las Pruebas
- **Consumo de Memoria:** Entre 228.7 MB y 320.8 MB, dependiendo de los permisos concedidos y las acciones realizadas.
- **Uso de CPU:** Se observó un uso elevado (100%) durante el procesamiento de imágenes.
- **Duración de la Batería:** Aunque no se midió directamente, se prevé un alto consumo durante el uso intensivo de CPU.

## Instalación y Configuración
### Requisitos
- Android Studio Bumblebee o superior.
- Android SDK 34 (Android 14).
- Dispositivo físico o emulador con Android 14.

### Instrucciones de Instalación
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/guidodev29/CamaraAndroid.git
