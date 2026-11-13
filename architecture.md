# Arquitectura de Carpetas - WolopWallet

## Estructura general del proyecto

```
WolopWallet/
├─ .gradle/
├─ .idea/
├─ .kotlin/
├─ build/
├─ gradle/
├─ build.gradle.kts
├─ gradle.properties
├─ gradlew
├─ gradlew.bat
├─ local.properties
├─ settings.gradle.kts
├─ AGENTS.md
├─ app/
   ├─ build/
   ├─ src/
   │  ├─ androidTest/
   │  ├─ main/
   │  │  ├─ assets/
   │  │  ├─ java/
   │  │  │  └─ com/
   │  │  │      └─ wolop/
   │  │  │          └─ wallet/
   │  │  │              ├─ Adapters/
   │  │  │              │  ├─ Home/
   │  │  │              │  └─ Introduction/
   │  │  │              ├─ data/
   │  │  │  │  ├─ BenefitMockData.kt
   │  │  │  │  └─ HomeMockData.kt
   │  │  │              ├─ keepRules/
   │  │  │  │  └─ rules.keep
   │  │  │              ├─ Screens/
   │  │  │  │  ├─ Introduction/
   │  │  │  │  │  ├─ Introduction.kt
   │  │  │  │  ├─ Login.kt
   │  │  │  │  ├─ Register.kt
   │  │  │  │  ├─ Welcome.kt
   │  │  │  │  └─ home/
   │  │  │  │     ├─ HomeActivity.kt
   │  │  │  │     └─ tabs/
   │  │  │  │        ├─ BeneficiosFragment.kt
   │  │  │  │        ├─ InicioFragment.kt
   │  │  │  │        ├─ MenuFragment.kt
   │  │  │  │        ├─ PerfilFragment.kt
   │  │  │  │        └─ QrFragment.kt
   │  │  │  └─ AndroidManifest.xml
   │  ├─ res/
   │  │  ├─ color/
   │  │  ├─ drawable/
   │  │  ├─ layout/
   │  │  ├─ menu/
   │  │  ├─ mipmap-anydpi-v26/
   │  │  ├─ mipmap-hdpi/
   │  │  ├─ mipmap-mdpi/
   │  │  ├─ mipmap-xhdpi/
   │  │  ├─ mipmap-xxhdpi/
   │  │  ├─ mipmap-xxxhdpi/
   │  │  ├─ values-night/
   │  │  └─ values/
   │  │     └─ strings.xml  (solo define app_name)
   │  └─ AndroidManifest.xml
   ├─ test/
   └─ build.gradle.kts
```

## Convenciones y detalles importantes

| Capa / Carpeta | Observación |
|---|---|
| **Screens/** | Archivos en **camelCase** español (ej. `btnEntrar`, `txtFrase`). Los layouts XML tienen texto duro en español; `strings.xml` solo contiene `app_name`. |
| **Adapters/** | Nombres de carpetas **capitalizados** (`Adapters`, `Screens`). |
| **Introduction** | Carga `banner1`, `banner2`, … usando `resources.getIdentifier` y se detiene al encontrar la primera brecha. Se deben agregar banners contiguamente (banner 5 solo después de banner 4). |
| **MainActivity** | Es un lanzador **redirect‑only** (no infla `activity_main.xml`). |
| **HomeActivity** | Contiene un `BottomNavigationView` y reemplaza fragmentos con `supportFragmentManager.replace(...)`. Los fragmentos se recrean al cambiar de pestaña, por lo que el estado no persiste. |
| **keepRules/** | Reglas R8 opcionales; AGP 9.5.1 concatena todos los archivos `.keep` de `app/src/main/keepRules/*.keep`. |
| **Resources** | Imágenes duplicadas en `res/drawable/` y `assets/`; el código solo referencia drawables. |
| **Navigation** | Navegación manual con `Intent` / `finish()`, no se usa el Navigation Component. |
| **Edge‑to‑Edge** | Cada actividad llama a `enableEdgeToEdge()` y aplica insets del sistema al view con `android:id="@+id/main"`. |

## Flujo de actividades

1. **MainActivity** → redirige a `screens.Welcome`.
2. **Welcome** → `screens.Login`.
3. **Login** → `screens.Introduction`.
4. **Introduction** → `screens.home.HomeActivity`.
5. **HomeActivity** alberga `BottomNavigationView` con 5 pestañas (Fragmentos): Inicio, Menú, Beneficios, Perfil, QR.

Cada tabFragment se recrea en la selección, por lo que el estado de la UI no se conserva al cambiar de pestaña.

## Capas de código

- **Presentation UI**: Fragments y Activities (layout XML con copy en español).
- **Data**: Mock data clases (`BenefitMockData.kt`, `HomeMockData.kt`).
- **Adapters**: Adapters específicos para listas en las pantallas Home e Introduction.
- **Keep rules**: Regla R8 para preservar miembros críticos de la minificación.

---
*Generado a partir de la exploración del proyecto WolopWallet.*