# Create API/Impl Module

Creates a new feature module following the project's API/Impl separation pattern.

## Usage

`/create-api-impl-module <moduleName>`

Example: `/create-api-impl-module analytics`

## Steps

1. Create directory structure:
   ```
   <moduleName>/
   ├── api/
   │   ├── build.gradle.kts
   │   └── src/main/java/merail/life/<moduleName>/api/
   │       └── I<ModuleName>Repository.kt
   └── impl/
       ├── build.gradle.kts
       └── src/main/java/merail/life/<moduleName>/impl/
           ├── di/<ModuleName>Module.kt
           └── repository/<ModuleName>Repository.kt
   ```

2. **`api/build.gradle.kts`** — use `library.plugin`, set namespace to `merail.life.<moduleName>.api`, add `implementation(projects.domain)`:
   ```kotlin
   plugins {
       alias(libs.plugins.library.plugin)
   }
   android {
       namespace = "merail.life.<moduleName>.api"
   }
   dependencies {
       implementation(projects.domain)
   }
   ```

3. **`impl/build.gradle.kts`** — use `library.plugin` + `ksp` + `hilt.gradle`, set namespace to `merail.life.<moduleName>.impl`, depend on `projects.<moduleName>.api` and `projects.domain`:
   ```kotlin
   plugins {
       alias(libs.plugins.library.plugin)
       alias(libs.plugins.ksp)
       alias(libs.plugins.hilt.gradle)
   }
   android {
       namespace = "merail.life.<moduleName>.impl"
   }
   dependencies {
       testImplementation(libs.junit)
       testImplementation(libs.kotlinx.coroutines.test)
       testImplementation(libs.mockk)
       implementation(libs.hilt.android)
       ksp(libs.hilt.compiler)
       implementation(projects.domain)
       implementation(projects.<moduleName>.api)
   }
   ```

4. **Repository interface** in `api` — `I<ModuleName>Repository` with suspend functions

5. **Hilt module** in `impl/di/` — `internal interface <ModuleName>Module` with `@Binds @Singleton` binding

6. **Repository implementation** in `impl/repository/` — `internal class <ModuleName>Repository @Inject constructor()`

7. Register in **`settings.gradle.kts`**:
   ```kotlin
   include(":<moduleName>")
   include(":<moduleName>:api")
   include(":<moduleName>:impl")
   ```

8. Add dependencies in **`app/build.gradle.kts`**:
   ```kotlin
   implementation(projects.<moduleName>.api)
   implementation(projects.<moduleName>.impl)
   ```

9. Run `./gradlew :<moduleName>:impl:compileDebugKotlin` to verify

## Conventions

- API module exposes only the interface — no Hilt, no implementation details
- Impl module is `internal` — only the DI module is `@InstallIn(SingletonComponent::class)`
- Only `:app` depends on `:impl` modules; other feature modules depend on `:api` only
- Typesafe project accessors: `projects.<moduleName>.api`
