package merail.life.result

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import merail.life.time.api.ITimeRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestResultViewModel {

    private lateinit var viewModel: ResultViewModel

    private val savedStateHandle = SavedStateHandle()

    private val timeRepository: ITimeRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { timeRepository.getTimeUntilNextDay() } returns flowOf(Pair("23:59:59", false))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isVictory and attemptsCount should be correctly parsed from SavedStateHandle`() {
        viewModel = ResultViewModel(
            savedStateHandle = savedStateHandle,
            timeRepository = timeRepository,
        )

        assertTrue(viewModel.isVictory)
        assertEquals(4, viewModel.attemptsCount)
    }

    @Test
    fun `timeUntilNextDay updates every second and sets isNextDay only at end`() = runTest(testDispatcher) {
        val timeFlow = MutableSharedFlow<Pair<String, Boolean>>(extraBufferCapacity = 1)
        coEvery { timeRepository.getTimeUntilNextDay() } returns timeFlow

        viewModel = ResultViewModel(
            savedStateHandle = savedStateHandle,
            timeRepository = timeRepository,
        )

        advanceUntilIdle()

        timeFlow.tryEmit(Pair("00:00:02", false))
        advanceUntilIdle()

        assertEquals("00:00:02", viewModel.timeUntilNextDay.value)
        assertFalse(viewModel.isNextDay.value)

        timeFlow.tryEmit(Pair("00:00:01", false))
        advanceUntilIdle()

        assertEquals("00:00:01", viewModel.timeUntilNextDay.value)
        assertFalse(viewModel.isNextDay.value)

        timeFlow.tryEmit(Pair("00:00:00", true))
        advanceUntilIdle()

        assertEquals("00:00:00", viewModel.timeUntilNextDay.value)
        assertTrue(viewModel.isNextDay.value)
    }
}
