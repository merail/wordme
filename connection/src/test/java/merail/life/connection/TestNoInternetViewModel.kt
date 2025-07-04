package merail.life.connection

import androidx.lifecycle.SavedStateHandle
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import merail.life.config.api.IConfigRepository
import merail.life.connection.state.ReloadingState
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.WordIdModel
import merail.life.domain.WordModel
import merail.life.domain.constants.IS_TEST_ENVIRONMENT
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.game.api.IGameRepository
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestNoInternetViewModel {

    private lateinit var viewModel: NoInternetViewModel
    private val savedStateHandle = SavedStateHandle().apply {
        set<Boolean>(IS_TEST_ENVIRONMENT, true)
    }

    private val configRepository: IConfigRepository = mockk()
    private val databaseRepository: IDatabaseRepository = mockk()
    private val storeRepository: IStoreRepository = mockk()
    private val timeRepository: ITimeRepository = mockk()
    private val gameRepository: IGameRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { configRepository.authAnonymously() } just Runs
        coEvery { configRepository.fetchInitialValues() } just Runs
        coEvery { configRepository.getIdsDatabasePassword() } returns flowOf("test-password")

        coEvery { databaseRepository.initIdsDatabase(any()) } just Runs
        coEvery { timeRepository.getDaysSinceStartCount() } returns flowOf(5)
        coEvery { databaseRepository.getDayWordId(any()) } returns WordIdModel(42)
        coEvery { databaseRepository.getDayWord(any()) } returns WordModel("дубль")

        coEvery { storeRepository.getDaysSinceStartCount() } returns flowOf(5)
        coEvery { storeRepository.loadKeyForms() } returns flowOf(emptyList())
        coEvery { gameRepository.setDayWord(any()) } just Runs
        coEvery { gameRepository.setKeyForms(any()) } just Runs

        coEvery { storeRepository.saveDaysSinceStartCount(any()) } just Runs
        coEvery { storeRepository.removeKeyForms() } just Runs
        coEvery { storeRepository.getLastVictoryDay() } returns flowOf(3)
        coEvery { storeRepository.resetVictoriesRowCount() } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchInitialData sets ReloadingState to Success`() = runTest(testDispatcher) {
        viewModel = NoInternetViewModel(
            savedStateHandle = savedStateHandle,
            configRepository = configRepository,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.fetchInitialData()

        advanceUntilIdle()

        assertEquals(ReloadingState.Success, viewModel.reloadingState.value)
    }

    @Test
    fun `fetchInitialData sets ReloadingState to None on NoInternetConnectionException`() = runTest(testDispatcher) {
        coEvery { configRepository.fetchInitialValues() } throws NoInternetConnectionException()

        viewModel = NoInternetViewModel(
            savedStateHandle = savedStateHandle,
            configRepository = configRepository,
            databaseRepository = databaseRepository,
            storeRepository = storeRepository,
            timeRepository = timeRepository,
            gameRepository = gameRepository,
        )

        viewModel.fetchInitialData()

        advanceUntilIdle()

        assertEquals(ReloadingState.None, viewModel.reloadingState.value)
    }
}
