package merail.life.wordme

import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import merail.life.config.api.IConfigRepository
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.WordIdModel
import merail.life.domain.WordModel
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.game.api.IGameRepository
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val configRepository: IConfigRepository = mockk()
    private val databaseRepository: IDatabaseRepository = mockk()
    private val storeRepository: IStoreRepository = mockk()
    private val timeRepository: ITimeRepository = mockk()
    private val gameRepository: IGameRepository = mockk()

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { configRepository.authAnonymously() } just Runs

        coEvery { configRepository.fetchInitialValues() } just Runs

        coEvery { configRepository.getIdsDatabasePassword() } returns flowOf("test-password")

        every { databaseRepository.initIdsDatabase("test-password") } just Runs

        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)

        coEvery { databaseRepository.getDayWordId(any()) } returns WordIdModel(42)

        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(0)

        coEvery { gameRepository.setDayWord(any()) } just Runs

        coEvery { gameRepository.setKeyForms(any()) } just Runs

        coEvery { databaseRepository.getDayWord(any()) } returns WordModel("дубль")

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
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        val state = viewModel.mainState.value

        assertEquals(MainState.Success, state)
    }

    @Test
    fun `init failure - no internet - sets mainState to NoInternetConnection`() = runTest(testDispatcher) {
        coEvery { configRepository.fetchInitialValues() } throws NoInternetConnectionException()

        viewModel = MainViewModel(
            configRepository = configRepository,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        val state = viewModel.mainState.value

        assertEquals(MainState.NoInternetConnection, state)
    }

    @Test
    fun `when lastSinceStartDaysCount equals current, loads keyForms`() = runTest(testDispatcher) {
        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(1)
        coEvery { storeRepository.loadKeyForms() } returns flowOf(listOf(mockk()))

        viewModel = MainViewModel(
            configRepository = configRepository,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
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
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
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
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
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
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        advanceUntilIdle()

        coVerify(exactly = 0) { storeRepository.resetVictoriesRowCount() }
    }
}