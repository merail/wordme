# Add Server Endpoint

Adds a new endpoint to the server module following the existing three-layer pattern.

## Usage

`/add-server-endpoint <route> <params> <returnType>`

Example: `/add-server-endpoint getStats userId:Int StatsModel`

## Steps

1. Read the current files to understand existing patterns:
   - `server/api/src/main/java/merail/life/server/api/IServerRepository.kt`
   - `server/impl/src/main/java/merail/life/server/impl/repository/ServerApi.kt`
   - `server/impl/src/main/java/merail/life/server/impl/repository/ServerRepository.kt`

2. Add the new method to `IServerRepository` interface in `:server:api`

3. Add the raw HTTP call to `ServerApi` — use `serverHttpClient.httpClient.get("<route>")` with `parameter()` for query params, `.body()` for deserialization. Non-2xx responses automatically throw `ResponseException` (client has `expectSuccess = true`) — no manual status checking needed

4. Add the implementation in `ServerRepository` — wrap in `withContext(Dispatchers.IO)`, convert raw response to domain model

5. Add unit tests to `server/impl/src/test/java/merail/life/server/impl/ServerRepositoryTest.kt` following the existing pattern:
   - Test successful response
   - Test exception propagation

6. Run `./gradlew :server:impl:test` to verify

## Conventions

- `ServerApi` methods return raw types (String, Boolean, etc.)
- `ServerRepository` wraps them into domain models from `:domain`
- All HTTP calls use GET with query parameters
- Test names use backtick syntax: `` `method does something` ``
