package merail.life.wordme

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import merail.life.config.api.IConfigRepository
import merail.life.core.log.IWordMeLogger
import merail.life.server.api.IServerRepository
import merail.life.domain.WordModel
import merail.life.game.api.IGameRepository
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import merail.life.wordme.state.MainState
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val configRepository: IConfigRepository = mockk()
    private val serverRepository: IServerRepository = mockk()
    private val storeRepository: IStoreRepository = mockk()
    private val timeRepository: ITimeRepository = mockk()
    private val gameRepository: IGameRepository = mockk()
    private val logger: IWordMeLogger = mockk(relaxed = true)

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { configRepository.authAnonymously() } just Runs

        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)

        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(0)

        coEvery { gameRepository.setDayWord(any()) } just Runs

        coEvery { gameRepository.setKeyForms(any()) } just Runs

        coEvery { serverRepository.getDayWord(any()) } returns WordModel("дубль")

        coEvery { storeRepository.loadKeyForms() } returns flowOf(emptyList())

        coEvery { storeRepository.saveDaysSinceStartCount(any()) } just Runs

        coEvery { storeRepository.removeKeyForms() } just Runs

        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(0)

        coEvery { storeRepository.resetVictoriesRowCount() } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init success - sets mainState to Success`() = runTest(testDispatcher) {
        viewModel = MainViewModel(
            configRepository = configRepository,
            serverRepository = serverRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
            logger = logger,
        )

        advanceUntilIdle()

        assertEquals(MainState.Success, viewModel.mainState.value)
    }

    @Test
    fun `init failure - sets mainState to LoadingError`() = runTest(testDispatcher) {
        coEvery { configRepository.authAnonymously() } throws RuntimeException()

        viewModel = MainViewModel(
            configRepository = configRepository,
            serverRepository = serverRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
            logger = logger,
        )

        advanceUntilIdle()

        assertEquals(MainState.LoadingError, viewModel.mainState.value)
    }

    @Test
    fun `when lastSinceStartDaysCount equals current, loads keyForms`() = runTest(testDispatcher) {
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { storeRepository.loadKeyForms() } returns flowOf(listOf(mockk()))

        viewModel = MainViewModel(
            configRepository = configRepository,
            serverRepository = serverRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
            logger = logger,
        )

        advanceUntilIdle()

        coVerify { gameRepository.setKeyForms(any()) }
        coVerify(exactly = 0) { storeRepository.removeKeyForms() }
    }

    @Test
    fun `when lastSinceStartDaysCount not equals current, resets keyForms and saves new day`() = runTest(testDispatcher) {
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(0)
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)

        viewModel = MainViewModel(
            configRepository = configRepository,
            serverRepository = serverRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
            logger = logger,
        )

        advanceUntilIdle()

        coVerify { storeRepository.removeKeyForms() }
        coVerify { storeRepository.saveDaysSinceStartCount(1) }
        coVerify(exactly = 0) { gameRepository.setKeyForms(any()) }
    }

    @Test
    fun `when more than 1 day since last victory, resets victories row count`() = runTest(testDispatcher) {
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(3)
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(3)
        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(1)

        viewModel = MainViewModel(
            configRepository = configRepository,
            serverRepository = serverRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
            logger = logger,
        )

        advanceUntilIdle()

        coVerify { storeRepository.resetVictoriesRowCount() }
    }

    @Test
    fun `when 1 or less days since last victory, does not reset victories row count`() = runTest(testDispatcher) {
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(2)
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(2)
        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(1)

        viewModel = MainViewModel(
            configRepository = configRepository,
            serverRepository = serverRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
            logger = logger,
        )

        advanceUntilIdle()

        coVerify(exactly = 0) { storeRepository.resetVictoriesRowCount() }
    }
}