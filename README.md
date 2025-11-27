# FitLife Tracker

**Fecha:** 29-08-2025  
**Nombres de los estudiantes:** Vicente Lizana y Benjamin Vivero  
**Profesor:** Giovanni Caceres

---

1. **Nombre de la aplicación:** *FitLife Tracker*

2. **Propósito y problema que resuelve:** * Ayuda a los usuarios a centralizar su vida deportiva, permitiéndoles organizar rutinas de entrenamiento, calcular métricas de salud (IMC) y registrar su progreso físico visualmente. La aplicación busca combatir la desorganización y la falta de motivación, ofreciendo una herramienta moderna y eficiente para que cualquier usuario pueda mantener un control básico de su bienestar y tener consciencia de su evolución física.

3. **Pantallas iniciales (Activities):** * **`MainActivity`:** Pantalla de información con créditos y descripción del proyecto.
   * **`Login`:** Gestión de acceso seguro mediante autenticación con Google o modo invitado.
   * **`home`:** Panel principal que lista las rutinas guardadas y muestra la ubicación actual.
   * **`Rutina`:** Formulario reutilizable para crear nuevas rutinas o editar las existentes.
   * **`RutinaDetalleActivity`:** Vista de lectura para consultar los ejercicios sin riesgo de edición.
   * **`Imc` y `CamaraActivity`:** Herramientas para el cálculo de salud y registro fotográfico respectivamente.

4. **Navegación entre pantallas:** * Uso de **Intents explícitos** para la transición fluida entre todas las Activities.  
   * Transferencia de datos complejos (objetos `RutinaModel`) entre pantallas mediante la interfaz `Serializable` y `Intent.putExtra`.  
   * Gestión del ciclo de vida y pila de actividades mediante `finish()` para asegurar un flujo de navegación lógico (ej. al guardar o cerrar sesión).

5. **Componentes de Android previstos (Implementados):** * **Arquitectura:** Patrón MVVM (Model-View-ViewModel) con Repositorios para separar lógica y diseño.
   * **Comunicación:** Intents y LiveData para la actualización reactiva de la interfaz.
   * **Servicios en la Nube:** Firebase Realtime Database para almacenamiento y Firebase Auth para usuarios.
   * **Hardware Nativo:** Integración de CameraX para fotos y FusedLocationProvider para GPS.
