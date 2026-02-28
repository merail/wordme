# Write Unit Tests

Generates unit tests for a given class following the project's testing conventions.

## Usage

`/write-unit-tests <filePath>`

Example: `/write-unit-tests server/impl/src/main/java/merail/life/server/impl/repository/ServerRepository.kt`

## Steps

1. Read the target file to understand its public API, constructor dependencies, and behavior

2. Determine test file location:
   - Source: `<module>/src/main/java/merail/life/<package>/<Class>.kt`
   - Test: `<module>/src/test/java/merail/life/<package>/<Class>Test.kt`

3. Read existing tests in the same module for style reference

4. Generate test class following these conventions:

   **For Repository/plain classes:**
   ```kotlin
   class <Class>Test {
       // MockK mocks for all constructor dependencies
       private lateinit var dependency: DependencyType
       private lateinit var sut: ClassUnderTest

       @Before
       fun setUp() {
           dependency = mockk()
           sut = ClassUnderTest(dependency)
       }

       @Test
       fun `method does expected thing`() = runTest {
           coEvery { dependency.method() } returns value
           val result = sut.method()
           assertEquals(expected, result)
           coVerify { dependency.method() }
       }
   }
   ```

   **For ViewModels:**
   ```kotlin
   @OptIn(ExperimentalCoroutinesApi::class)
   class <ViewModel>Test {
       private lateinit var viewModel: TheViewModel
       // MockK mocks for all constructor dependencies
       private val testDispatcher = StandardTestDispatcher()

       @Before
       fun setUp() {
           Dispatchers.setMain(testDispatcher)
           // coEvery stubs for init block calls
       }

       @After
       fun tearDown() {
           Dispatchers.resetMain()
       }

       @Test
       fun `test name`() = runTest(testDispatcher) {
           viewModel = TheViewModel(/* named params */)
           advanceUntilIdle()
           assertEquals(expected, viewModel.state.value)
       }
   }
   ```

5. Cover these scenarios:
   - Happy path for each public method
   - Error/exception propagation
   - Edge cases (empty inputs, boundary values)
   - For ViewModels: state transitions, init block behavior

6. Run `./gradlew :<module>:test` to verify all tests pass

## Conventions

- Framework: JUnit 4 + MockK + kotlinx-coroutines-test
- Test names: backtick syntax `` `descriptive name in English` ``
- Use `coEvery`/`coVerify` for suspend functions, `every`/`verify` for regular
- Use `mockk(relaxed = true)` only for loggers and similar non-critical deps
- Use `runTest` for coroutine tests, `runTest(testDispatcher)` for ViewModel tests
- Named parameters when constructing the class under test
- For error/exception test cases, use `RuntimeException()` — no domain-specific exceptions exist in this project
- ViewModel loading failure state is `MainState.LoadingError` (not `NoInternetConnection` — that was removed)
