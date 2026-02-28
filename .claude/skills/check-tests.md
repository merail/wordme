# Check Tests

Runs unit tests and analyzes failures.

## Usage

`/check-tests` — run all tests
`/check-tests <module>` — run tests for a specific module (e.g., `server:impl`, `app`, `game:impl`)

## Steps

1. Run the appropriate Gradle command:
   - All: `./gradlew test`
   - Module: `./gradlew :<module>:test`

2. If all tests pass — report success with count summary

3. If tests fail:
   - Parse the output to identify failing test class and method
   - Read the failing test file
   - Read the source file being tested
   - Analyze the mismatch between test expectations and actual code behavior
   - Suggest a fix — determine whether the test is outdated (update test) or the code has a bug (fix code)
   - Ask the user before applying fixes

## Notes

- Test reports are at `<module>/build/reports/tests/test<Variant>UnitTest/index.html`
- Common failure causes in this project:
  - Test mocks an old method signature after refactoring
  - ViewModel init block calls changed but test setUp stubs weren't updated
  - Missing `coEvery` stub for a new dependency call
- Error simulation convention: use `RuntimeException()` — no domain-specific exceptions (`NoInternetConnectionException` etc.) exist
- `MainState.LoadingError` is the ViewModel failure state for initial loading errors
