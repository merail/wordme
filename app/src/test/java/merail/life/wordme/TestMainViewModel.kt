package merail.life.wordme

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import merail.life.core.log.IWordMeLogger
import merail.life.game.api.IGameLoader
import merail.life.wordme.state.MainState
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestMainViewModel {

    private val gameLoader: IGameLoader = mockk()
    private val logger: IWordMeLogger = mockk(relaxed = true)

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { gameLoader.loadInitialData() } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init success - sets mainState to Success`() = runTest(testDispatcher) {
        viewModel = MainViewModel(
            gameLoader = gameLoader,
            logger = logger,
        )

        assertEquals(MainState.Loading, viewModel.mainState.value)

        advanceUntilIdle()

        assertEquals(MainState.Success, viewModel.mainState.value)
    }

    @Test
    fun `init failure - sets mainState to LoadingError`() = runTest(testDispatcher) {
        coEvery { gameLoader.loadInitialData() } throws RuntimeException()

        viewModel = MainViewModel(
            gameLoader = gameLoader,
            logger = logger,
        )

        advanceUntilIdle()

        assertEquals(MainState.LoadingError, viewModel.mainState.value)
    }
}
