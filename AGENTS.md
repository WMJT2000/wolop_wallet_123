# AGENTS.md

## Project
- Single-module Android app (`:app`): Kotlin + XML Views + AppCompat/Material 3. No Compose, View Binding, Navigation component, DI, or network layer.
- `applicationId`/namespace `com.wolop.wallet`; `minSdk 24`, `compileSdk`/`targetSdk 37`, Java source/target 11 (`app/build.gradle.kts`).
- AGP `9.5.1` on Gradle `9.6.0` (wrapper). Kotlin compiles through AGP 9's built-in Kotlin support: there is no `kotlin-android` plugin or `kotlinOptions` block.
- Dependencies live in `gradle/libs.versions.toml` and are referenced as `libs.*`.

## App flow
- `MainActivity` is a redirect-only launcher (no `setContentView`): with an active session it opens `screens.home.HomeActivity`, otherwise `screens.Welcome` -> (botón "Entrar") `screens.Introduction` -> `screens.Login` -> `screens.home.HomeActivity`. `Introduction` solo se muestra al pulsar "Entrar" en Welcome; al terminar/saltar pasa a Login.
- `HomeActivity` hosts five tabs with a hand-built bottom bar (no `BottomNavigationView`) + `supportFragmentManager.replace(...)`; each tab is a plain Fragment in `screens.home.tabs`. Tabs are recreated on every selection, so fragment state does not survive tab switches. Selection is drawn by the `bottom_nav_item` color selector (`state_selected`), so tab labels/icons need `duplicateParentState="true"`.
- Navigation is manual `Intent`/`finish()` calls, not Navigation Component. `Register` sends users back to `Login`.
- Every activity calls `enableEdgeToEdge()` and applies system-bar insets to the root view `R.id.main`. A new activity layout must use `android:id="@+id/main"` or that listener fails.

## Layout notes
- No hay variantes `layout-land/`: una sola vista por pantalla (`res/layout/` plano). Las pantallas con mucho contenido vertical están envueltas en `NestedScrollView` (`fillViewport="true"`, scroll `none`) para que en horizontal sigan siendo alcanzables (Welcome, QR, Menú, Contactos, etc.).
- `screen_welcome.xml` es una columna centrada con padding adaptado (logo/phrase/botón). `Menú`: cards `64dp` + pie de políticas; portrait ahora apila desde arriba.
- Al probar en horizontal con adb: `settings put system user_rotation 1` (y `0` para portrait) + `accelerometer_rotation 0`; restaurar `accelerometer_rotation 1` al terminar.

## Dialog system (new — `ui/dialogs/`)
- Un solo `AppDialogFragment` + `DialogManager.show(fm, DialogConfig)` + **Fragment Result API** (`REQUEST_KEY = "wolop_dialog_result"`, acciones `POSITIVE` / `NEGATIVE` / `DISMISSED`). Sin callbacks directos ni singletons con Activities.
- Tipos: `SUCCESS` / `ERROR` / `ALERT` (un botón) · `QUESTION` (Cancelar + Aceptar) · `LOADING` (GIF animado, sin botones, cerrado por código vía `DialogManager.dismissLoading(fm)`).
- `dialog_app.xml`: icon 84dp, título serif bold, mensaje, divider dorado, botones pill; LOADING no tiene botones ni divider.
- Back y toque exterior NO cierran. Botones se deshabilitan tras el primer toque.
- `DemoDialogsActivity` es la pantalla de prueba (no conectada a lógica real); lanzarla con `adb shell am start -n com.wolop.wallet/.screens.DemoDialogsActivity`.
- No se toca lógica de negocio existente ni assets/iconos de alerta.

## Local data (Room)
- Persistence is Room `2.8.5` (`room-runtime` + `room-ktx`) with `room-compiler` run through KSP (`com.google.devtools.ksp` `2.3.12`); versions come from `libs.versions.toml`. There is no DI framework, no repository interface, and no network layer.
- Layering: `data/local/entities/Usuario.kt` -> `data/local/dao/UsuarioDao.kt` -> `data/local/AppDatabase.kt` -> `data/repository/UsuarioRepository.kt`, exposed through the `data/repository/DatabaseProvider.kt` factory.
- `AppDatabase` is a manual singleton (`getDatabase(context)`, double-checked on `INSTANCE`), DB file `wolop_wallet.db`, `version = 1`, one table `usuarios`, no type converters and no migrations.
- `Usuario` columns: `id` (autoGenerate primary key), `nombre`, `correo`, `telefono` (nullable), `fechaRegistro` (defaults to `System.currentTimeMillis()`). Changing this entity requires bumping `version` and adding a `Migration`, otherwise Room throws at open time.
- `UsuarioDao` mixes `suspend` write/read methods with one `Flow<List<Usuario>>` query (`obtenerTodos`). `UsuarioRepository` just forwards those calls; screens get it via `DatabaseProvider.getUsuarioRepository(context)`.
- `Welcome.onCreate` no longer touches the DB; there is no debug seeding anymore, so the `usuarios` table only gets rows through the login flow.
- `@Database(exportSchema = true)` is set but no `room.schemaLocation` KSP argument is configured, so no schema JSON is emitted and `app/schemas` does not exist. Add a `ksp { arg("room.schemaLocation", ...) }` block (and commit `app/schemas`) before relying on schema diffing or `autoMigrations`.
- UI screens still use in-memory mock data (`data/mock/HomeMockData.kt`, `data/mock/BenefitMockData.kt`, `data/mock/TarjetaMockData.kt`, `data/mock/AuthMockData.kt`); only `Usuario` is actually persisted, and the Perfil tab overlays the identity columns (nombre/correo/telefono) onto `HomeMockData.user` via `HomeMockData.conIdentidad(...)`.

## Login and session (simulated)
- There is no backend and no password column. `data/mock/AuthMockData.kt` holds the only account: `prueba@wolop.com` / `wolop123`, plus the display name `Micaela Alcocer` and phone `099 999 9999`.
- `screens.Login` validates empty fields and the credentials against `AuthMockData.credencialesValidas(...)`, surfacing errors through `layoutCorreo.error` / `layoutContrasena.error`. On success it upserts the `Usuario` row (`obtenerUsuarioPorCorreo` then insert or `actualizarUsuario`), waits ~600 ms to fake a request, and opens `HomeActivity` with `NEW_TASK or CLEAR_TASK`.
- `data/session/SessionManager.kt` persists the session in `SharedPreferences("wolop_session")` (`logged_in`, `user_id`, `user_correo`, `user_nombre`). `MainActivity` reads it to skip Welcome/Login; `HomeActivity` bounces back to `Welcome` when it is missing, so a logged-out state can't be reached through the back stack.
- "Cerrar sesión" in the Perfil tab (`filaCerrarSesion`) clears the preferences and restarts `Welcome` with `CLEAR_TASK`; the DB row is intentionally kept, so logging in again reuses it.
- To reset everything while testing: `adb shell pm clear com.wolop.wallet` (clears both preferences and the Room file).

## QR generation
- `data/qr/` is a self-contained generator (no camera, no scanning): `QRGenerator.generar(contenido, QRConfig)` returns an ARGB_8888 `Bitmap` using ZXing `com.google.zxing:core` (declared as `libs.zxing.core`, version in `libs.versions.toml`). It touches `android.graphics`, so it only runs on device, not in JVM unit tests.
- `QRConfig` carries the knobs: `tamaño` (px), `colorPrincipal`, `colorFondo`, `estilo`, `margen`, optional `usarDegradado`/`colorDegradado` (linear gradient shader) and optional `logo`/`tamañoLogo`. `generar` validates: non-blank content, `tamaño > 0`, `margen >= 0`, `tamañoLogo in 0f..0.25f`.
- `QRStyle` has three renderings, all drawn cell by cell over the ZXing matrix: `CLASSIC` (squares), `ROUNDED` (rounded cells), `DOTS` (circles). Error correction is always level `H`, which is what leaves room for a centered logo; the logo gets a rounded background patch using `colorFondo`.
- `QrFragment` is still a scaffold: it generates a hardcoded `"WolopWallet:123456"` payload at 600 px in `CLASSIC`, sets a light-gray `ImageView` background for debugging, and logs under tag `QR_TEST`. Replace the payload with the session/user id, drop the `setBackgroundColor(Color.LTGRAY)`, and use the app palette (`#F8F2ED` page, `#3B2418` foreground).
- `fragment_qr.xml` is aligned with the app palette: marble page (`@drawable/fondo_principal`), marble card with gold stroke (`#C6A664`, radius 26dp), dynamic QR (unchanged generation pipeline), name/petáles count bound at runtime from session/mock.
- Generation is synchronous inside `onViewCreated`; keep sizes modest or move the call to a background dispatcher if the payload/config grows.

## Layout map (res/layout)

`res/layout/` is flat by design (Android does not support subfolders there). Naming convention: `screen_*` = activities del flujo de acceso, `activity_home` = contenedor de tabs, `fragment_*` = pantallas/tabs, `item_*` = vistas de lista.

- `activity_home.xml` — tabs de Home + bottom navigation (`homeFragmentContainer`).
- `screen_welcome.xml` — Welcome (logo + ENTRAR / CREAR CUENTA).
- `screen_introduction.xml` — onboarding (`viewPagerIntroduction`, `btnSaltar`, `btnSiguiente`).
- `screen_login.xml` — login (`edtCorreo`, `edtContrasena`).
- `screen_register.xml` — registro.
- `fragment_inicio.xml` — tab Inicio (`recyclerCreaciones`, `scrollInicio`).
- `fragment_beneficios.xml` — tab Beneficios (`recyclerBeneficios`).
- `fragment_qr.xml` — tab QR (`imgCodigoQR`).
- `fragment_menu.xml` — tab Menú (`cardTarjeta`, `cardLocales`, `cardContactos`, `cardTutoriales`, `cardAyuda`).
- `fragment_perfil.xml` — tab Perfil (`filaCerrarSesion`).
- `fragment_tarjeta.xml` — sub-pantalla Tarjeta (`imgIdTarjeta`, `txtEstadoValor`, `imgCodigoTarjeta`, `txtSaldoValor`).
- `fragment_locales.xml` — sub-pantalla Locales (`recyclerLocales`).
- `fragment_contactos.xml` — sub-pantalla Contactos (placeholder "Hola").
- `dialog_app.xml` — diseño único del diálogo GEMMA (`txtTituloDialog`, `txtMensajeDialog`, `imgIconDialog`, `gifIconoCarga`, `btnPositiveDialog`, `btnNegativeDialog`).
- `activity_demo_dialogs.xml` — pantalla de demostración de diálogos (`/screens.DemoDialogsActivity`).
- `item_beneficio.xml` — fila de Beneficios; `item_creacion.xml` — card de creaciones (Inicio); `item_local.xml` — tarjeta de una tienda (Locales); `item_introduction_banner.xml` — página del onboarding.

## Source/resource quirks
- Source folders are capitalized (`Screens/`, `Adapters/`), but Kotlin packages are lowercase `com.wolop.wallet.screens...`; `com.wolop.wallet.Adapters.Introduction` is capitalized. Manifest entries use `.screens.*`. Copy existing package declarations exactly.
- Layout files follow the naming convention documented in "Layout map (res/layout)".
- UI copy is Spanish and hardcoded in layout XML; `strings.xml` only defines `app_name`. IDs are camelCase Spanish (`btnEntrar`, `txtFrase`). Keep that pattern; `HardcodedText` lint warnings are expected.
- Images are duplicated under `res/drawable/` and `assets/`; code only references drawables. Edit `res/drawable/` (the assets copies are unused).
- `Introduction` loads `banner1`, `banner2`, ... via `resources.getIdentifier` and stops at the first gap. Add banners contiguously (`banner5` only after `banner4`).
- `activity_main.xml` was removed (it was an unused template; `MainActivity` never inflated it).

## Commands (Windows PowerShell)
- Build: `.\gradlew.bat assembleDebug`
- Unit tests: `.\gradlew.bat :app:testDebugUnitTest`
- Single test: `.\gradlew.bat :app:testDebugUnitTest --tests "com.wolop.wallet.ExampleUnitTest"`
- Instrumented tests (device/emulator required): `.\gradlew.bat :app:connectedDebugAndroidTest`
- Lint: `.\gradlew.bat lint`
- Gradle configuration cache is enabled (`gradle.properties`); daemon JVM criteria are pinned to Java 25 in `gradle/gradle-daemon-jvm.properties` (the foojay resolver downloads it). App bytecode still targets Java 11.

## Build notes
- Room codegen is the only annotation processor: `AppDatabase_Impl` / `UsuarioDao_Impl` land in `app/build/generated/ksp/debug/kotlin/...`. Editing an entity/DAO does not always invalidate that output — a normal rebuild can keep a stale impl and fail with `Class 'AppDatabase_Impl' is not abstract and does not implement abstract base class member: fun usuarioDao(): UsuarioDao`. Fix it by deleting `app/build` (or `.\gradlew.bat clean`) and rebuilding.
- Release uses AGP 9's `optimization { enable = true; packageScope = ... }` DSL, not `isMinifyEnabled`.
- Add R8 rules as `app/src/main/keepRules/*.keep`; AGP combines every `.keep` file in that directory.
- `local.properties` is machine-specific (SDK path); never treat it as portable config or commit it.
- There is no README or CI workflow; verify changes with the Gradle tasks above.
