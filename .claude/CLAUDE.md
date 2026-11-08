# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WordMe is a native Android daily word game (Russian, 5-letter words, 6 attempts — see `ROWS_COUNT`/`COLUMNS_COUNT` in `GameViewModel`) built with Kotlin, Jetpack Compose, and Material 3. Word data comes from a backend server API.

- **Min SDK**: 30, **Target/Compile SDK**: 36, **JVM**: 17
- **Application ID**: `merail.life.wordme`

## Build Commands

```bash
./gradlew assembleDebug                    # Debug build
./gradlew assembleRelease                  # Release build (requires signing config)
./gradlew test                             # All unit tests
./gradlew :game:impl:test                  # Single module unit tests
./gradlew :game:impl:testDebugUnitTest --tests "merail.life.game.impl.TestGameViewModel"   # Single test class
./gradlew connectedAndroidTest             # Instrumented tests (requires device/emulator)
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=merail.life.wordme.GameScreenTest
./gradlew lint                             # Android lint (no detekt/ktlint configured)
./gradlew :app:generateBaselineProfile     # Baseline profile via :profiling on managed device pixel6Api34
```

The build reads `local.properties` directly (in `:server:impl` and `:time:api`), so it fails without it — see Local Setup.

## Architecture

**Clean Architecture with API/Impl module separation.** Feature modules expose interfaces in `:feature:api` and implementations in `:feature:impl`. The app module composes everything together.

### Module Dependency Pattern

```
:app → every module (the only consumer of :*:impl)
:x:impl → :x:api, other features' :api modules, :domain, :core
:result, :rules, :stats, :connection (single-module screens) → :*:api, :design, :core
```

Cross-feature dependencies go through api modules only: e.g. `:game:impl` uses `:server:api`, `:store:api`, `:time:api`; `:time:impl` uses `:server:api` (the countdown start date comes from the server).

### Key Modules

| Module | Role |
|--------|------|
| `:app` | Composition root, navigation, Firebase setup, Hilt entry point |
| `:core` | Shared utilities, Hilt qualifiers, logging, extensions |
| `:design` | Compose Material 3 theme, shared UI components; `components/` package for reusable composables (e.g. `CrossIconButton`) |
| `:domain` | Pure data models (no Android dependencies) |
| `:game:api/impl` | Game logic, keyboard UI, result board |
| `:server:api/impl` | Backend API client (Ktor) — day word fetching, word validation |
| `:store:api/impl` | DataStore preferences with Protobuf |
| `:time:api/impl` | Time management via Play Services Time API |
| `:config:api/impl` | Firebase anonymous auth (`authAnonymously()` — the only call) |
| `:result` | Victory/result screen |
| `:rules` | "How to play" bottom sheet (game rules with letter cell examples) |
| `:stats` | Statistics/history screen |
| `:connection` | Loading error screen — shown on any initial loading failure (no internet, 403, etc.) |
| `:profiling` | Baseline profile generation |

The top-level `navigation/` directory is stale build output, not a module (not in `settings.gradle.kts`).

### Startup & Data Flow

1. `WordMeApplication.onCreate` sets a `TrustedTimeClient` into `ITimeSource` (only if `USE_TRUSTED_TIME_CLIENT` and GMS is recent enough); otherwise time falls back to `System.currentTimeMillis()`.
2. `MainActivity` holds the splash screen while `MainViewModel.mainState` is `Loading`.
3. `MainViewModel.init` does all initial loading: Firebase anonymous auth → days since start (server countdown start date + current time) → fetch day word with `id = days + 1` → restore saved key forms if it's the same day, otherwise clear them → reset the victory streak if the last win was more than 1 day ago. Any failure → `MainState.LoadingError`.
4. `WordMeNavHost` picks `LoadingErrorRoute` or `GameRoute` as start destination based on that state.

`IGameRepository` is an **in-memory** holder (`MutableStateFlow`s), not persistence: `MainViewModel` preloads the day word and key forms into it, and `GameViewModel` reads them via `.first()`. Its getters use `filterNotNull()`, so they suspend until something is set. Persistence lives in `:store`.

**Day rollover** happens inside the running game: `GameViewModel` collects `ITimeRepository.getTimeUntilNextDay()` (ticks every second) and, when `isNextDay`, re-fetches the word and resets the board. With `REDUCE_TIME_UNTIL_NEXT_DAY` (debug only) the clock starts at 23:59:40 and the day counter is shifted through the mutable companion fields `ITimeRepository.countdownStartRealTime` / `debugDaysSinceStartCount`.

### Navigation

Each screen module exposes, in its `*Navigation.kt`: a `@Serializable` route implementing `merail.life.core.NavigationRoute`, a `NavController.navigateToX()` extension, and a `NavGraphBuilder.xScreen()` extension. The graph is assembled in `app/.../navigation/WordMeNavGraph.kt`. `Result`, `Rules` and `Stats` are `dialog<>` destinations closed with `popBackStack`; `Game` and `LoadingError` are `composable<>`.

### Server Module Layering

`:server:impl` has a three-layer structure: `ServerHttpClient` (Ktor client config with retries/timeouts) → `ServerApi` (raw HTTP calls) → `ServerRepository` (implements `IServerRepository`, wraps responses into domain models). All classes are `internal`, only `IServerRepository` from `:server:api` is exposed.

`ServerHttpClient` has `expectSuccess = true` — non-2xx responses (403, 404, etc.) automatically throw `ResponseException` instead of returning the body. Also sends `X-WordMe-Token` header from `BuildConfig.ACCESS_TOKEN`.

### Store Module

Key forms are stored via Protobuf (`store/impl/src/main/proto/*.proto`, java/kotlin lite). `store/impl/build.gradle.kts` has custom wiring that makes each `ksp<Variant>Kotlin` task depend on `generate<Variant>Proto` — keep it when editing that build file, otherwise KSP/Hilt fails on the generated classes.

### Patterns

- **MVVM**: `@HiltViewModel` ViewModels with `StateFlow` state management
- **Hilt DI**: Each module has its own `*Module.kt` providing bindings as `@Singleton`
- **Repository pattern**: Interface in api module, implementation in impl module
- **Errors in suspend code**: use `suspendableRunCatching` from `:core` (rethrows `CancellationException`) instead of `runCatching`; log through `IWordMeLogger` with a `TAG` constant
- **Convention plugin**: `LibraryConventionPlugin` (included build `plugin/`, applied as `alias(libs.plugins.library.plugin)`) sets compileSdk/minSdk/JVM 17 and derives `namespace` from the module path (`:game:impl` → `merail.life.game.impl`). It does **not** enable Compose — UI modules add `alias(libs.plugins.kotlin.compose)` and `buildFeatures { compose = true }` themselves.

## Testing

- Unit tests: JUnit 4 + MockK + kotlinx-coroutines-test. Test classes are named `Test<ClassName>` (e.g. `TestGameViewModel`), test methods use backtick names. Simulate errors with `RuntimeException()` — there are no domain-specific exceptions.
- Instrumented tests exist only in `app/src/androidTest`. They use `HiltTestRunner` (swaps in `HiltTestApplication`) and `HiltTestActivity` from the `debug` source set, and render `WordMeNavHost` directly.

## Dependencies & Frameworks

- **UI**: Jetpack Compose (BOM-managed), Material 3, Navigation Compose (type-safe routes via kotlinx-serialization)
- **DI**: Hilt with KSP
- **Networking**: Ktor Client (OkHttp engine, ContentNegotiation, HttpTimeout, HttpRequestRetry) + kotlinx-serialization-json
- **Preferences**: DataStore + Protobuf
- **Firebase**: Analytics, Crashlytics, Auth
- **Testing**: JUnit 4, MockK, kotlinx-coroutines-test; Espresso + UIAutomator for instrumented tests

## Local Setup

The project requires `local.properties` with:
- `domainUrl` — backend server host (e.g. `wordme.duckdns.org`), used in `BuildConfig.DOMAIN_URL` in `:server:impl`
- `accessToken` — token for `X-WordMe-Token` header, used in `BuildConfig.ACCESS_TOKEN` in `:server:impl`
- `reduceTimeUntilNextDay` — debug flag for testing daily reset logic (always `false` in release)
- `useTrustedTimeClient` — set `true` to enable Play Services TrustedTimeClient (anti-cheat); set `false` on emulators where TrustedTimeClient returns stale cached time. Used for both debug and release builds via `BuildConfig.USE_TRUSTED_TIME_CLIENT` in `:time:api`; in CI set via `USE_TRUSTED_TIME_CLIENT` GitHub secret.
- Release signing properties (`releaseKeystorePassword`, `releaseKeystoreAlias`, `releaseKeyPassword`) for local release builds; the keystore is expected at `app/keystore.keystore`. In CI signing comes from `RELEASE_KEYSTORE_*` env vars instead.
- A `google-services.json` in the `:app` module for Firebase

## CI/CD

GitHub Actions with two workflows:
- **build-on-push**: Runs on push to `main`/`develop` — runs unit tests, builds debug APK
- **release**: Manual dispatch — runs unit tests, builds signed release APK; version is passed as `-PVERSION_CODE` / `-PVERSION_NAME` (defaults `1` / `"1.0"` locally)

## Code Style

- Kotlin official code style (`kotlin.code.style=official`)
- Every argument/parameter goes on its own line with a trailing comma — even for a single argument:
  ```kotlin
  .padding(
      top = 12.dp,
  )            // not .padding(top = 12.dp)
  ```
- Typesafe project accessors enabled (`projects.game.api` instead of `project(":game:api")`)
- Gradle version catalog at `gradle/libs.versions.toml` for all dependency management
