package merail.life.time.impl

import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import merail.life.server.api.IServerRepository
import merail.life.time.api.ITimeRepository
import merail.life.time.api.ITimeSource
import merail.life.time.impl.repository.TimeRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.*

class TestTimeRepository {

    private lateinit var serverRepository: IServerRepository
    private lateinit var timeSource: ITimeSource
    private lateinit var repository: TimeRepository

    private val zone = ZoneId.systemDefault()

    @Before
    fun setUp() {
        serverRepository = mockk()
        timeSource = mockk()
        repository = TimeRepository(
            appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            serverRepository = serverRepository,
            timeSource = timeSource,
        )
    }

    @Test
    fun `getDaysSinceStartCount returns correct number of days`() = runTest {
        val now = LocalDate.of(2025, 7, 1).atStartOfDay()
        val millis = now.atZone(zone).toInstant().toEpochMilli()
        every { timeSource.getCurrentUnixEpochMillis() } returns flowOf(millis)

        coEvery { serverRepository.getGameCountdownStartDate() } returns "29.06.2025"

        ITimeRepository.debugDaysSinceStartCount = 0

        val result = repository.getDaysSinceStartCount().first()

        assertEquals(2, result)
    }

    @Test
    fun `getTimeUntilNextDay uses release logic`() = runTest {
        val now = LocalDate.of(2025, 7, 1).atTime(23, 59, 58)
        val millis = now.atZone(zone).toInstant().toEpochMilli()
        every { timeSource.getCurrentUnixEpochMillis() } returns flowOf(millis)

        val (timeString, isNextDay) = repository.getTimeUntilNextDay(
            reduceTimeFlag = false,
        ).first()

        assertEquals("00:00:02", timeString)
        assertEquals(false, isNextDay)
    }

    @Test
    fun `getTimeUntilNextDay uses debug logic`() = runTest {
        ITimeRepository.countdownStartRealTime = System.currentTimeMillis() - 20_000

        val (timeString, isNextDay) = repository.getTimeUntilNextDay(
            reduceTimeFlag = true,
        ).first()

        assertEquals("00:00:00", timeString)
        assertEquals(true, isNextDay)
    }
}