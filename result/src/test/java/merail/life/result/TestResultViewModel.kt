package merail.life.result

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import merail.life.time.api.ITimeRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestResultViewModel {

    private lateinit var viewModel: ResultViewModel

    private val timeRepository: ITimeRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isVictory and attemptsCount should be correctly parsed from SavedStateHandle`() {
        viewModel = ResultViewModel(
            savedStateHandle = SavedStateHandle(),
            timeRepository = timeRepository,
        )

        assertTrue(viewModel.isVictory)
        assertEquals(4, viewModel.attemptsCount)
    }

    @Test
    fun `timeUntilNextDay updates every second and sets isNextDay only at end`() = runTest(testDispatcher) {
        viewModel = ResultViewModel(
            savedStateHandle = SavedStateHandle(),
            timeRepository = timeRepository,
        )

        val localTimeRepository = mockk<ITimeRepository>()

        coEvery { localTimeRepository.getTimeUntilNextDay() } returnsMany listOf(
            "00:00:02" to false,
            "00:00:01" to false,
            "00:00:00" to true,
        )

        viewModel.startTimer(localTimeRepository)

        runCurrent()
        assertEquals("00:00:02", viewModel.timeUntilNextDay)
        assertFalse(viewModel.isNextDay)

        advanceTimeBy(1000)
        runCurrent()
        assertEquals("00:00:01", viewModel.timeUntilNextDay)
        assertFalse(viewModel.isNextDay)

        advanceTimeBy(1000)
        runCurrent()
        assertEquals("00:00:00", viewModel.timeUntilNextDay)
        assertTrue(viewModel.isNextDay)
    }
}
