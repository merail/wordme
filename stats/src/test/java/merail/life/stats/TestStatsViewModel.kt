package merail.life.stats

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import merail.life.store.api.IStoreRepository
import merail.life.store.api.model.StatsModel
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestStatsViewModel {

    private lateinit var viewModel: StatsViewModel

    private val storeRepository: IStoreRepository = mockk()

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
    fun `viewModel sets all values correctly from repository`() = runTest(testDispatcher) {
        val stats = StatsModel(
            victoriesCount = 8,
            gamesCount = 10,
            attemptsCount = 24,
            victoriesRowCount = 3,
            victoriesRowMaxCount = 5,
        )

        coEvery { storeRepository.getStats() } returns flowOf(stats)

        viewModel = StatsViewModel(storeRepository)

        advanceUntilIdle()

        assertEquals("80%", viewModel.victoriesPercent)
        assertEquals("3.0", viewModel.attemptsRatio)
        assertEquals("8", viewModel.victoriesCount)
        assertEquals("3", viewModel.victoriesRowCount)
        assertEquals("5", viewModel.victoriesRowMaxCount)
    }
}
