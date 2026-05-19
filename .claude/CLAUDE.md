# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WordMe is a native Android word game app built with Kotlin, Jetpack Compose, and Material 3. It uses a daily-puzzle mechanic with a backend server API for word data.

- **Min SDK**: 30, **Target/Compile SDK**: 36, **JVM**: 17
- **Application ID**: `merail.life.wordme`

## Build Commands

```bash
./gradlew assembleDebug                    # Debug build
./gradlew assembleRelease                  # Release build (requires signing config)
./gradlew test                             # All unit tests
./gradlew :game:impl:test                  # Single module unit tests
./gradlew connectedAndroidTest             # Instrumented tests (requires device/emulator)
```

## Architecture

**Clean Architecture with API/Impl module separation.** Feature modules expose interfaces in `:feature:api` and implementations in `:feature:impl`. The app module composes everything together.

### Module Dependency Pattern

```
:app → :feature:api + :feature:impl
:feature:impl → :feature:api → :domain
```

All impl modules are only depended on by `:app`. API modules define repository interfaces; impl modules provide Hilt-injected implementations.

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
| `:config:api/impl` | Firebase Auth and Firestore remote configuration |
| `:result` | Victory/result screen |
| `:stats` | Statistics/history screen |
| `:connection` | Loading error screen — shown on any initial loading failure (no internet, 403, etc.) |
| `:profiling` | Baseline profile generation |

### Server Module Layering

`:server:impl` has a three-layer structure: `ServerHttpClient` (Ktor client config with retries/timeouts) → `ServerApi` (raw HTTP calls) → `ServerRepository` (implements `IServerRepository`, wraps responses into domain models). All classes are `internal`, only `IServerRepository` from `:server:api` is exposed.

`ServerHttpClient` has `expectSuccess = true` — non-2xx responses (403, 404, etc.) automatically throw `ResponseException` instead of returning the body. Also sends `X-WordMe-Token` header from `BuildConfig.ACCESS_TOKEN`.

### Patterns

- **MVVM**: `@HiltViewModel` ViewModels with `StateFlow` state management
- **Hilt DI**: Each module has its own `*Module.kt` providing bindings as `@Singleton`
- **Repository pattern**: Interface in api module, implementation in impl module
- **Convention plugin**: `LibraryConventionPlugin` in `:plugin` applies shared Android library configuration

## Dependencies & Frameworks

- **UI**: Jetpack Compose (BOM-managed), Material 3, Navigation Compose
- **DI**: Hilt with KSP
- **Networking**: Ktor Client (OkHttp engine, ContentNegotiation, HttpTimeout, HttpRequestRetry) + kotlinx-serialization-json
- **Preferences**: DataStore + Protobuf
- **Firebase**: Analytics, Crashlytics, Auth, Firestore
- **Testing**: JUnit 4, MockK, kotlinx-coroutines-test; Espresso + UIAutomator for instrumented tests

## Local Setup

The project requires `local.properties` with:
- `domainUrl` — backend server host (e.g. `wordme.duckdns.org`), used in `BuildConfig.DOMAIN_URL` in `:server:impl`
- `accessToken` — token for `X-WordMe-Token` header, used in `BuildConfig.ACCESS_TOKEN` in `:server:impl`
- `reduceTimeUntilNextDay` — debug flag for testing daily reset logic
- `useTrustedTimeClient` — set `true` to enable Play Services TrustedTimeClient (anti-cheat); set `false` on emulators where TrustedTimeClient returns stale cached time. Used for both debug and release builds via `BuildConfig.USE_TRUSTED_TIME_CLIENT` in `:time:api`; in CI set via `USE_TRUSTED_TIME_CLIENT` GitHub secret.
- Release signing properties (`releaseKeystorePassword`, `releaseKeystoreAlias`, `releaseKeyPassword`) for local release builds
- A `google-services.json` in the `:app` module for Firebase

## CI/CD

GitHub Actions with two workflows:
- **build-on-push**: Runs on push to `main`/`develop` — runs unit tests, builds debug APK
- **release**: Manual dispatch — runs unit tests, builds signed release APK with version params

## Code Style

- Kotlin official code style (`kotlin.code.style=official`)
- Typesafe project accessors enabled (`projects.game.api` instead of `project(":game:api")`)
- Gradle version catalog at `gradle/libs.versions.toml` for all dependency management
