# FitLife Tracker

**Fecha:** 29-08-2025  
**Nombres de los estudiantes:** Vicente Lizana y Benjamin Vivero  
**Profesor:** Giovanni Caceres

---

1. **Nombre de la aplicación:**  
   *FitLife Tracker*

2. **Propósito y problema que resuelve:**  
   * Ayuda a los usuarios a organizar sus rutinas, calcular su IMC y recibir recomendaciones de ejercicio de manera simple y eficiente.

3. **Pantallas iniciales (Activities y Fragments):**  
   * **Activities:** LoginActivity y MainActivity  
   * **Fragments:** HomeFragment, IMCFragment, CrearRutinaFragment y EncontrarRutinaFragment  

4. **Navegación entre pantallas:**  
   * De LoginActivity a MainActivity mediante `Intent` con datos del usuario.  
   * Navigation Drawer dentro de MainActivity para cambiar entre fragments.  
   * `Bundle` para pasar datos entre fragments cuando sea necesario.

5. **Componentes de Android previstos:**  
   * Activities y Fragments mencionados.  
   * Intents para transmitir información entre Activities y Fragments.  
   * Base de datos SQLite mediante DBHelper para almacenar rutinas.
