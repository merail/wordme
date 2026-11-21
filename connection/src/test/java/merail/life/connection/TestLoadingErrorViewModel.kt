package merail.life.connection

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import merail.life.connection.state.ReloadingState
import merail.life.core.log.IWordMeLogger
import merail.life.game.api.IGameLoader
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestLoadingErrorViewModel {

    private lateinit var viewModel: LoadingErrorViewModel

    private val gameLoader: IGameLoader = mockk()
    private val logger: IWordMeLogger = mockk(relaxed = true)

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
    fun `fetchInitialData sets ReloadingState to Success`() = runTest(testDispatcher) {
        viewModel = LoadingErrorViewModel(
            gameLoader = gameLoader,
            logger = logger,
        )

        viewModel.fetchInitialData()

        advanceUntilIdle()

        assertEquals(ReloadingState.Success, viewModel.reloadingState.value)
    }

    @Test
    fun `fetchInitialData sets ReloadingState to None on failure`() = runTest(testDispatcher) {
        coEvery { gameLoader.loadInitialData() } throws RuntimeException()

        viewModel = LoadingErrorViewModel(
            gameLoader = gameLoader,
            logger = logger,
        )

        viewModel.fetchInitialData()

        advanceUntilIdle()

        assertEquals(ReloadingState.None, viewModel.reloadingState.value)
    }
}
