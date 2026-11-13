# Arquitectura de WolopWallet — guía para una IA externa

> **Propósito:** dar a una IA (o desarrollador) que llega sin contexto previo todo lo necesario para
> entender el proyecto y aplicar un cambio sin romper convenciones ni el build.
> **Ámbito:** describe el estado real del repositorio en `C:\Users\PREDATOR\AndroidStudioProjects\WolopWallet`.
> **Compañero:** `AGENTS.md` (raíz del repo) documenta lo mismo para agentes de código. Si difieren,
> tienen que actualizarse juntos. Este archivo manda sobre la estructura del proyecto; AGENTS.md manda
> sobre preferencias de trabajo.
> **Idioma del copy:** la app está en español (textos hardcodeados en XML). Los nombres técnicos de
> paquetes/carpetas respetan mayúsculas/minúsculas exactas del código (ver §6).

---

## 1. Resumen y stack

| Aspecto | Valor |
|---|---|
| Tipo | App Android de un solo módulo (`:app`) |
| UI | Kotlin + XML Views + AppCompat / Material 3. **Sin Compose, sin View Binding, sin Navigation Component, sin DI, sin capa de red.** |
| Identificador | `applicationId` / `namespace` = `com.wolop.wallet` |
| SDKs | `minSdk 24`, `compileSdk 37`, `targetSdk 37`; Java source/target 11 |
| Build | AGP `9.5.1` sobre Gradle `9.6.0` (wrapper). Configuración de caché habilitada (`gradle.properties`). Daemon JVM fijada a Java 25 (`gradle/gradle-daemon-jvm.properties`, foojay resolver la descarga). |
| Kotlin | Compilado vía el soporte Kotlin integrado de AGP 9: **no hay plugin `kotlin-android` ni bloque `kotlinOptions`.** |
| Persistencia | Room `2.8.5` (`room-runtime` + `room-ktx`), `room-compiler` vía KSP `2.3.12` |
| QR | ZXing `com.google.zxing:core` `3.5.4` (`libs.zxing.core`) |
| Dependencias | Centralizadas en `gradle/libs.versions.toml`, referenciadas como `libs.*` |
| VCS | Repositorio git (commit inicial "Initial commit - Proyecto Android") |

Configuración del módulo en `app/build.gradle.kts` (release usa el DSL `optimization { enable = true; packageScope = … }` de AGP 9, **no** `isMinifyEnabled`; las reglas R8 van en `app/src/main/keepRules/*.keep` y AGP concatena todos los `.keep`).

---

## 2. Estructura de carpetas

`res/layout/` es plano por diseño (Android no admite subcarpetas ahí). Convención de nombres:

- `screen_*` → pantallas del flujo de acceso (activities).
- `activity_home` → contenedor de tabs + bottom bar.
- `fragment_*` → tabs y sub-pantallas (fragments).
- `item_*` → filas/celdas para RecyclerView.

```
WolopWallet/
├── .git/
├── .gradle/                      # estado de build local (no tocar a mano)
├── .idea/
├── .kotlin/
├── build/                        # salidas del build raíz
├── gradle/
│   ├── libs.versions.toml        # versiones y coordenadas de dependencias
│   ├── gradle-daemon-jvm.properties
│   └── wrapper/                  # gradle-wrapper.properties (dist Gradle 9.6.0)
├── gradle.properties             # configuration cache ON
├── settings.gradle.kts
├── build.gradle.kts              # build raíz (sin plugins de app)
├── gradlew / gradlew.bat
├── local.properties              # ruta SDK de ESTA máquina — NO portable, NO commitear
├── AGENTS.md                     # guía para agentes de código (mantener sync con este archivo)
├── architecture.md               # este documento
└── app/
    ├── build.gradle.kts          # config del módulo
    └── src/main/
        ├── AndroidManifest.xml   # MainActivity LAUNCHER + Welcome/Login/Register/Introduction/HomeActivity
        ├── keepRules/rules.keep  # reglas R8 (se combinan todas las .keep del directorio)
        ├── assets/               # 36 PNG duplicados de res/drawable — NO se usan desde código; editar res/drawable
        └── java/com/wolop/wallet/
            ├── MainActivity.kt               # launcher redirect-only (no infla layout)
            ├── Adapters/                     # carpetas capitalizadas, paquetes "com.wolop.wallet.Adapters.*"
            │   ├── Home/BenefitsAdapter.kt
            │   ├── Home/CreationsAdapter.kt
            │   ├── Home/LocalesAdapter.kt
            │   └── Introduction/IntroductionAdapter.kt
            ├── Screens/                      # carpeta capitalizada, paquete minúscula "…screens…"
            │   ├── Welcome.kt
            │   ├── Introduction.kt           # onboarding (ViewPager2 + banners + indicadores)
            │   ├── Login.kt
            │   ├── Register.kt
            │   └── home/
            │       ├── HomeActivity.kt       # hosts tabs + bottom bar manual
            │       └── tabs/
            │           ├── InicioFragment.kt
            │           ├── BeneficiosFragment.kt
            │           ├── QrFragment.kt
            │           ├── MenuFragment.kt
            │           ├── PerfilFragment.kt
            │           ├── TarjetaFragment.kt    # sub-pantalla desde Menú
            │           ├── LocalesFragment.kt    # sub-pantalla desde Menú
            │           └── ContactosFragment.kt  # sub-pantalla desde Menú (placeholder "Hola")
            └── data/
                ├── local/
                │   ├── entities/Usuario.kt        # tabla `usuarios`
                │   ├── dao/UsuarioDao.kt          # suspend + un Flow<List<Usuario>>
                │   └── AppDatabase.kt             # singleton manual, wolop_wallet.db, version = 1
                ├── repository/
                │   ├── UsuarioRepository.kt       # forwarding fino al DAO
                │   └── DatabaseProvider.kt        # factory getUsuarioRepository(context)
                ├── session/SessionManager.kt      # SharedPreferences "wolop_session"
                ├── mock/                          # datos en memoria hasta que exista backend
                │   ├── AuthMockData.kt            # única cuenta: prueba@wolop.com / wolop123
                │   ├── HomeMockData.kt            # user base + conIdentidad(...) overlay
                │   ├── BenefitMockData.kt
                │   ├── LocalesMockData.kt
                │   └── TarjetaMockData.kt
                └── qr/                            # generador QR autocontenido (solo render, no cámara)
                    ├── QRGenerator.kt
                    ├── QRConfig.kt
                    └── QRStyle.kt
```

Recursos en `app/src/main/res/`: `color/bottom_nav_item.xml`, `drawable/` (XML shapes `bg_*`/`icon_*` + PNG), `drawable-nodpi/fondo_tarjeta_marble.png`, `layout/` (ver §5), `menu/bottom_nav_home.xml` (**sin uso**, la bottom bar es manual), `mipmap*` (launcher), `values/{strings,colors,themes}.xml`, `values-night/themes.xml`, `xml/{backup_rules,data_extraction_rules}.xml`.

Estado de `res/` actual relevante: 68 archivos en `drawable/` (incluye `bg_circle_siguiente.xml`, el círculo blanco del botón "Siguiente" de Introduction) frente a 36 en `assets/`, todos duplicados de drawable y **no referenciados desde código**.

---

## 3. Flujo de navegación (real)

No hay Navigation Component: toda la navegación es `Intent` + `finish()` manuales.

```
MainActivity (redirect-only, sin setContentView)
   │
   ├─ SessionManager.estaLogueado() == true ──► screens.home.HomeActivity   (NEW_TASK | CLEAR_TASK)
   │
   └─ false ──► screens.Welcome
                    │
                    ├─ "ENTRAR" ──► screens.Introduction ──(SALTAR / botón circular / último banner)──► screens.Login
                    │
                    └─ "CREAR CUENTA" ──► screens.Register ──(al terminar)──► screens.Login
                                                        │
                                             Login (valida contra AuthMockData,
                                             upsert de Usuario en Room, ~600 ms de espera fake)
                                                        │
                                                        ▼
                                             HomeActivity (NEW_TASK | CLEAR_TASK)
                                                        │
              ┌─────────────────────────────────────────┼──────────────────────────────┐
              │  Bottom bar manual: navInicio · navBeneficios · navQr · navMenu · navPerfil
              │  (selección vía isSelected + color selector bottom_nav_item; los fragments
              │   se RECREAN en cada selección — el estado no sobrevive al cambio de tab)
              └─────────────────────────────────────────┼──────────────────────────────┘
                                                        │
   Menú ──► HomeActivity.abrirSubPantalla(fragment) ──► TarjetaFragment / LocalesFragment / ContactosFragment
           (replace + addToBackStack; back vuelve a la tab; pulsar el mismo tab cierra sub-pantallas)
                                                        │
   Perfil ──► filaCerrarSesion ──► SessionManager.cerrarSesion() + restart de Welcome (CLEAR_TASK)
              (el registro en Room SE CONSERVA; el siguiente login lo reutiliza)
```

Reglas de negocio del flujo:

- **Login** valida campos vacíos y credenciales contra `AuthMockData` (`prueba@wolop.com` / `wolop123`; nombre `Micaela Alcocer`, teléfono `099 999 9999`). Los errores se muestran en `layoutCorreo.error` / `layoutContrasena.error`. En éxito, hace upsert del `Usuario` (`obtenerUsuarioPorCorreo` → insert o `actualizarUsuario`) antes de navegar.
- **Introduction** carga `banner1..N` con `resources.getIdentifier` y se detiene en la **primera brecha** (numeración contigua; si existe `banner4` deben existir `banner1..3`). Si no encuentra banners, salta directo a Login. El botón circular alterna `icon_intro_next` → `icon_intro_ok` en el último banner.
- **HomeActivity** sin sesión rebota a Welcome con `CLEAR_TASK`; por back stack no se llega a Home deslogueado.
- **Registrar un activity nuevo** exige `android:exported="false"` (salvo LAUNCHER) en `AndroidManifest.xml`.

---

## 4. Capa de datos

### 4.1 Room (único dato persistido)

```
data/local/entities/Usuario.kt   id (autoGenerate PK) · nombre · correo · telefono? · fechaRegistro (default now)
data/local/dao/UsuarioDao.kt     métodos suspend + 1 query Flow<List<Usuario>> obtenerTodos()
data/local/AppDatabase.kt        singleton manual (getDatabase(context), double-checked) · wolop_wallet.db · version = 1 · tabla `usuarios`
data/repository/UsuarioRepository.kt   forwarding de llamadas del DAO
data/repository/DatabaseProvider.kt    factory: DatabaseProvider.getUsuarioRepository(context)
```

Notas:

- `@Database(exportSchema = true)` pero **no** hay `ksp { arg("room.schemaLocation", …) }` → no se emiten JSON de esquema ni existe `app/schemas/`. Antes de usar `autoMigrations`/diffing, añadir el argumento.
- **Cambiar la entidad exige bump de `version` + `Migration`**; si no, Room lanza al abrir. Reset total en test: `adb shell pm clear com.wolop.wallet`.
- Known gotcha KSP: tras editar entity/DAO puede quedar un `AppDatabase_Impl` desactualizado en `app/build/generated/ksp/debug/` y fallar con "not abstract and does not implement…". Arreglo: borrar `app/build` (o `.\gradlew.bat clean`) y recompilar.

### 4.2 Sesión (simulada)

`SessionManager` sobre `SharedPreferences("wolop_session")`: claves `logged_in`, `user_id`, `user_correo`, `user_nombre`. Lo leen `MainActivity` (redirect) y `HomeActivity` (guardia). `cerrarSesion()` hace `clear()` — la fila de Room se mantiene.

### 4.3 Mocks (todo lo demás es en memoria)

- `AuthMockData`: única cuenta de prueba (ver §3).
- `HomeMockData`: modelo `User` base de la UI + `conIdentidad(nombre, correo, telefono)` que superpone los datos reales de Room sobre él (lo usa Perfil).
- `BenefitMockData`, `LocalesMockData`, `TarjetaMockData`: arrays/benefcios/locales/tarjeta para tabs.
- **Solo `Usuario` persiste de verdad.** Beneficios, tarjetas, locales, etc. son fixtures.

### 4.4 QR

`data/qr/` autocontenido, solo generación (no cámara): `QRGenerator.generar(contenido, QRConfig)` devuelve un `Bitmap` ARGB_8888 con ZXing. `QRConfig`: `tamaño` (px), `colorPrincipal`, `colorFondo`, `estilo`, `margen`, opcionales `usarDegradado`/`colorDegradado` y `logo`/`tamañoLogo` (se valida `tamañoLogo in 0f..0.25f`, margen ≥ 0, tamaño > 0). Corrección de errores siempre **H** (deja sitio al logo centrado). Estilos `QRStyle`: `CLASSIC` / `ROUNDED` / `DOTS` (celda a celda sobre la matriz). Toca `android.graphics` → solo corre en dispositivo.

Estado conocido: `QrFragment` aún genera `"WolopWallet:123456"` hardcodeado a 600 px `CLASSIC`, con `setBackgroundColor(Color.LTGRAY)` de depuración y log `QR_TEST` — scaffold a reemplazar por el id de sesión + paleta `#F8F2ED` / `#3B2418`.

---

## 5. Pantallas y layouts

Los layouts viven planos en `app/src/main/res/layout/`. IDs son camelCase en español; el copy va hardcodeado en el XML.

| Layout | Activity/Fragment | Elementos clave |
|---|---|---|
| `screen_welcome.xml` | Welcome | logo, `btnEntrar`, `btnCrearCuenta` |
| `screen_introduction.xml` | Introduction | `viewPagerIntroduction`, `btnSiguiente` (circular), `btnSaltar`, `layoutIndicadores` |
| `screen_login.xml` | Login | `edtCorreo`, `edtContrasena`, `layoutCorreo`, `layoutContrasena` |
| `screen_register.xml` | Register | campos de registro → vuelve a Login |
| `activity_home.xml` | HomeActivity | `main`, `homeFragmentContainer`, `navInicio` `navBeneficios` `navQr` `navMenu` `navPerfil` |
| `fragment_inicio.xml` | Inicio | `scrollInicio`, `recyclerCreaciones` (lista de `item_creacion.xml`) |
| `fragment_beneficios.xml` | Beneficios | `recyclerBeneficios` (`item_beneficio.xml`) |
| `fragment_qr.xml` | QR | `imgCodigoQR` (scaffold) |
| `fragment_menu.xml` | Menú | `cardTarjeta`, `cardLocales`, `cardContactos`, `cardTutoriales`, `cardAyuda` |
| `fragment_perfil.xml` | Perfil | identidad superpuesta desde Room, `filaCerrarSesion` |
| `fragment_tarjeta.xml` | Tarjeta (sub) | `imgIdTarjeta`, `txtEstadoValor`, `imgCodigoTarjeta`, `txtSaldoValor` |
| `fragment_locales.xml` | Locales (sub) | `recyclerLocales` (`item_local.xml`) |
| `fragment_contactos.xml` | Contactos (sub) | placeholder "Hola" |
| `item_introduction_banner.xml` | página del ViewPager de Introduction | imagen + claim |

Drawables XML de ejemplo (patrón a imitar al crear shapes): `bg_circle_gold.xml` (óvalo `#FAF5EF` + stroke `#C0964E`), `bg_avatar_circulo.xml`, `bg_indicador_activo.xml` (óvalo `#C99A48`, 9 dp), `bg_boton_canjear.xml`, `bg_pill_*.xml`, `bg_circle_siguiente.xml` (botón blanco de Introduction). Iconos vectoriales de entrada de texto: `icon_*_input.xml`.

`res/values/strings.xml` solo define `app_name`. `HardcodedText` de lint es esperado. `colors.xml` solo `black`/`white` (el resto son hex directos en los XML). `bottom_nav_item.xml` en `res/color/` es el selector (`state_selected`) que pinta labels/iconos de la bottom bar — los hijos necesitan `duplicateParentState="true"`.

---

## 6. Convenciones y gotchas

1. **Copy en español hardcodeado** en XML; `strings.xml` casi vacío. IDs camelCase en español (`btnEntrar`, `txtFrase`, `filaCerrarSesion`). Mantenerlo.
2. **Mayúsculas/minúsculas exactas**: carpetas fuente `Screens/` y `Adapters/` van capitalizadas; paquetes van en minúscula (`com.wolop.wallet.screens…`, `com.wolop.wallet.Adapters.Introduction`). El manifest usa `.screens.*`. `MainActivity` vive en `com.wolop.wallet`. Copia exacta de los `package`/`import` existentes o falla la compilación.
3. **Edge-to-edge**: toda activity llama `enableEdgeToEdge()` y aplica insets al root `@+id/main`. **Un layout nuevo de activity DEBE usar `android:id="@+id/main"`** o el listener no encuentra la vista.
4. **`assets/` ≠ `res/drawable/`**: 36 imágenes duplicadas en `assets/` no las referencia ningún código. **Edita siempre `res/drawable/`.**
5. **`res/menu/bottom_nav_home.xml` no se usa** (bottom bar manual en `activity_home.xml`). No borres sin comprobar referencias.
6. **Introduction**: banners contiguos (`getIdentifier("banner$i")` para en la primera brecha). Indicadores y botón `btnSiguiente` se leen por ID en Kotlin.
7. **Tabs se recrean** en cada selección (no uses `Fragment`s con estado interno). Las sub-pantallas sí usan `addToBackStack`.
8. **npm/lint**: `HardcodedText` esperado; no "arreglarlo" moviendo copy a strings sin decisión de diseño.
9. **QR**: nivel siempre H; `tamañoLogo ≤ 0.25`; no lanzar generación pesada en main thread si crece el payload.
10. **KSP/Room**: tras tocar entity/DAO, si falla con impl desactualizada → borrar `app/build` y recompilar.
11. **`local.properties`** es de esta máquina; nunca lo copies como config ni lo commitees.
12. **`activity_main.xml` ya no existe** (era el template; no lo restaures).
13. **`icon_benefit1.png` sigue en uso** en `BenefitMockData.kt` aunque Introduction ya no lo use.

---

## 7. Build y entorno (máquina actual)

Comandos (PowerShell, en la raíz del repo):

```powershell
.\gradlew.bat assembleDebug                                     # APK debug → app/build/outputs/apk/debug/app-debug.apk
.\gradlew.bat :app:testDebugUnitTest                            # tests unitarios JVM
.\gradlew.bat :app:testDebugUnitTest --tests "com.wolop.wallet.ExampleUnitTest"
.\gradlew.bat :app:connectedDebugAndroidTest                    # requiere dispositivo/emulador
.\gradlew.bat lint
```

**Gotcha real de esta máquina (verificado):** `GRADLE_USER_HOME` apunta a `D:\AndroidData\.gradle`, que es de **solo lectura** para este usuario. Ejecutar `.\gradlew.bat assembleDebug` falla con `FileNotFoundException … gradle-9.6.0-bin.zip.lck (Acceso denegado)`, y el Gradle extraído directamente falla con `Failed to load native library 'native-platform.dll'`.

Solución que funciona: redirigir `GRADLE_USER_HOME` a una carpeta escribible antes de invocar el wrapper (los metadatos se re-resuelven; el build funciona):

```powershell
$env:GRADLE_USER_HOME = "$env:LOCALAPPDATA\Temp\opencode\gradle-home"   # o similar escribible
.\gradlew.bat assembleDebug
```

Configuro nada globalmente — la variable solo vive en tu sesión de PowerShell.

Otras notas de build: el daemon va fijado a Java 25 vía `gradle/gradle-daemon-jvm.properties` (foojay la descarga si falta); el bytecode de la app sigue en Java 11. Cambios de recursos XML (`layout/`, `drawable/`) recompilan sin tocar Kotlin — los `UP-TO-DATE` del reporte con configuration cache pueden ser engañosos; **verifica el APK resultante**, no confíes en el "todo al día".

Limpieza: `.\gradlew.bat clean` o borrar `app/build`. Reset de datos de la app en el dispositivo: `adb shell pm clear com.wolop.wallet`.

---

## 8. Verificación en dispositivo con `adb`

Receta usada y probada en este proyecto (dispositivo físico Infinix X6886):

```powershell
# 1) Compilar e instalar
$env:GRADLE_USER_HOME = "$env:LOCALAPPDATA\Temp\opencode\gradle-home"
.\gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk

# 2) Lanzar desde cero
adb shell am force-stop com.wolop.wallet
adb shell monkey -p com.wolop.wallet -c android.intent.category.LAUNCHER 1

# 3) ¿Qué actividad está al frente?
adb shell dumpsys activity activities | Select-String "topResumedActivity"

# 4) Localizar vistas clicables por bounds
adb shell uiautomator dump /sdcard/ui.xml
adb pull /sdcard/ui.xml $env:TEMP\ui.xml
# parsear los atributos bounds="[x1,y1][x2,y2]" y resource-id del nodo buscado

# 5) Interactuar y capturar
adb shell input tap <x> <y>
adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png $env:TEMP\screen.png
```

Caso real (13-nov-2026): para validar el cambio del botón "Siguiente" de Introduction se instaló el APK, se llegó a la pantalla con `Welcome → ENTRAR` (bounds del botón en el dump: `[113,2003][967,2116]` → tap en `540,2060`), y se confirmó visualmente el círculo blanco con el chevron dorado (`tint #D0A04C`) en vez de `icon_benefit1`.

Notas: `exec-out screencap > archivo.png` en PowerShell corrompe el binario (lo guarda con encoding); usar `screencap -p /sdcard/…` + `adb pull`. `uiautomator dump` requiere pantalla estable; tras un `input tap` espera 1–2 s. Si la tarea se relanza y vuelve a Welcome es porque no hay sesión (comportamiento esperado).

---

## 9. Checklist para aplicar un cambio (IA externa)

1. **Localiza por ID, no por nombre de archivo**: busca el ID del XML (`findViewById(R.id.…)`) y su layout dueño con `grep`/búsqueda de texto (`R.id.<id>`). El ID lleva el prefijo generado `com.wolop.wallet:id/`.
2. **Respeta convenciones** (§6): camelCase español, `@+id/main` en activities, mayúsculas exactas de paquetes, sin texto en strings.xml.
3. **Si añades imágenes**: drawable en `res/drawable/` (no en `assets/`), minúsculas + `_`, sin mayúsculas; si es un shape simple, prefiere XML `<shape>` sobre PNG.
4. **Si tocas entity/DAO de Room**: bump de `version` + migración, o documenta el reset con `pm clear`. Después borra `app/build` si KSP se desactualiza.
5. **Si tocas build**: edita `gradle/libs.versions.toml` (versiones) o `app/build.gradle.kts` (config de módulo); no dupliques versiones en otros archivos.
6. **Compila** con el `GRADLE_USER_HOME` redirigido (§7), **instala** con `adb install -r` y **verifica visualmente** navegando al flujo real (§8). Un cambio de UI no está terminado hasta que se ve en el dispositivo/emulador.
7. **No toques**: `assets/`, `local.properties`, `.git/`, `.idea/`, `.gradle/`, `app/build/` (salida), `icon_benefit1.png`, `res/menu/bottom_nav_home.xml` (sin uso pero referencia), `activity_home.xml` si solo cambias una tab.
8. **Revisa AGENTS.md** tras cambios estructurales y mantenlos sincronizados con este documento.
9. **Reset rápido de estado** entre pruebas: `adb shell pm clear com.wolop.wallet` (borra sesión + Room).
10. **Criterio de hecho**: build verde + APK instalado + screenshot de la pantalla afectada tomada y revisada.

---

## 10. Mapa rápido "quiero X → toco Y"

| Quiero… | Archivo(s) |
|---|---|
| Cambiar el círculo/botón "Siguiente" de Introduction | `res/layout/screen_introduction.xml`, shape `res/drawable/bg_circle_siguiente.xml`, lógica en `Screens/Introduction.kt` |
| Añadir otro banner de onboarding | `res/drawable/bannerN.png` con N contiguo + nada más (se autodescubre) |
| Cambiar el orden/estilo de tabs de Home | `res/layout/activity_home.xml` (IDs `nav*`), `Screens/home/HomeActivity.kt` |
| Cambiar colores de iconos/labels de la bottom bar | `res/color/bottom_nav_item.xml` + `drawable/` de iconos |
| Añadir un campo al perfil/usuario | `data/local/entities/Usuario.kt` (+bump version/migración), `PerfilFragment.kt` |
| Cambiar credenciales de prueba | `data/mock/AuthMockData.kt` |
| Cambiar textos de cualquier pantalla | el `fragment_*.xml`/`screen_*.xml` dueño del ID — **no** crees entries en `strings.xml` |
| Nueva sub-pantalla desde Menú | nuevo `fragment_x.xml` + `XFragment.kt` en `Screens/home/tabs/` + `abrirSubPantalla()` en `MenuFragment.kt` + `addToBackStack` ya lo da HomeActivity |
| Cambiar estilo del QR | `data/qr/QRConfig.kt` / `QRStyle.kt` / `QRGenerator.kt` + `QrFragment.kt` |
| Reglas de ofuscación release | `app/src/main/keepRules/*.keep` |

---

*Generado a partir de una exploración directa del repositorio (vistas de fuentes, layouts y build) el 13-nov-2026. Si añades/renombras archivos, actualiza este documento y `AGENTS.md` en el mismo cambio.*
