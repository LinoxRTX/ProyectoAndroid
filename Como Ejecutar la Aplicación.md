# FitLife Tracker — Guía de Ejecución

Este proyecto es un **MVP** en desarrollo: actualmente solo contiene las vistas y la estructura de carpetas. No incluye lógica funcional (login, cálculos, base de datos) aún.

---

## Requisitos previos

- **Android Studio** (última versión estable)
- **Android SDK** y **Build Tools** instalados (API ≥ 33 recomendada)
- **Teléfono Android** con:
  - Versión ≥ `minSdkVersion` (21+ recomendado)
  - Opciones de desarrollador y **Depuración USB** activadas
- **Cable USB** o ADB por Wi-Fi configurado

---

## Paso a paso para ejecutar la app

1. **Abrir el proyecto**
   - Clona el repositorio o descarga el ZIP.
   - Abre la carpeta en Android Studio.
   - Espera a que termine el **Gradle Sync**.

2. **Configurar el dispositivo**
   - Activa **Opciones de desarrollador** en tu teléfono.
   - Activa **Depuración USB**.
   - Conecta el teléfono al PC y acepta la clave RSA cuando se pida.

3. **Seleccionar dispositivo en Android Studio**
   - En la barra superior, elige tu dispositivo en el desplegable de ejecución.

4. **Ejecutar la app**
   - Haz clic en **Run ▶** (o presiona `Shift + F10`).
   - Android Studio compilará y enviará la app al teléfono.

5. **Probar en el dispositivo**
   - Busca el ícono de la app y ábrela.
   - Verifica que las pantallas de **Login** y **Home** se muestren correctamente.

---

## Notas
- Por ahora, los botones y formularios **no tienen funcionalidad**; solo se validará que las vistas se carguen.
- Más adelante se agregará la lógica de login, base de datos y cálculos.

---

## Checklist rápido ( o = Si, x = No)
- [ ] Android Studio instalado  
- [ ] SDKs y herramientas configuradas  
- [ ] Dispositivo conectado y detectado  
- [ ] Proyecto abierto sin errores de Gradle  
- [ ] App compila y se ejecuta en el teléfono  

