package merail.life.time.impl

import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import merail.life.server.api.IServerRepository
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

        val result = repository.getDaysSinceStartCount().first()

        assertEquals(2, result)
    }

    @Test
    fun `getDaysSinceStartCount fetches countdown start date once`() = runTest {
        val now = LocalDate.of(2025, 7, 1).atStartOfDay()
        val millis = now.atZone(zone).toInstant().toEpochMilli()
        every { timeSource.getCurrentUnixEpochMillis() } returns flowOf(millis)

        coEvery { serverRepository.getGameCountdownStartDate() } returns "29.06.2025"

        repository.getDaysSinceStartCount().first()
        val result = repository.getDaysSinceStartCount().first()

        assertEquals(2, result)
        coVerify(exactly = 1) { serverRepository.getGameCountdownStartDate() }
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
    fun `getTimeUntilNextDay detects next day when midnight tick is skipped`() = runTest {
        val beforeMidnight = LocalDate.of(2025, 7, 1).atTime(23, 59, 50)
        val afterMidnight = LocalDate.of(2025, 7, 2).atTime(0, 5, 0)
        every { timeSource.getCurrentUnixEpochMillis() } returnsMany listOf(
            flowOf(beforeMidnight.atZone(zone).toInstant().toEpochMilli()),
            flowOf(afterMidnight.atZone(zone).toInstant().toEpochMilli()),
            flowOf(afterMidnight.plusSeconds(1).atZone(zone).toInstant().toEpochMilli()),
        )

        val result = repository.getTimeUntilNextDay(
            reduceTimeFlag = false,
        ).take(3).toList()

        assertEquals(
            listOf(
                "00:00:10" to false,
                "23:55:00" to true,
                "23:54:59" to false,
            ),
            result,
        )
    }

    @Test
    fun `getTimeUntilNextDay does not detect next day on first tick`() = runTest {
        val now = LocalDate.of(2025, 7, 1).atTime(23, 59, 59, 500_000_000)
        val millis = now.atZone(zone).toInstant().toEpochMilli()
        every { timeSource.getCurrentUnixEpochMillis() } returns flowOf(millis)

        val (timeString, isNextDay) = repository.getTimeUntilNextDay(
            reduceTimeFlag = false,
        ).first()

        assertEquals("00:00:00", timeString)
        assertEquals(false, isNextDay)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getTimeUntilNextDay uses debug logic`() = runTest {
        val now = LocalDate.of(2025, 7, 1).atStartOfDay()
        val millis = now.atZone(zone).toInstant().toEpochMilli()
        every { timeSource.getCurrentUnixEpochMillis() } returns flowOf(millis)

        coEvery { serverRepository.getGameCountdownStartDate() } returns "29.06.2025"

        val debugRepository = TimeRepository(
            appScope = backgroundScope,
            serverRepository = serverRepository,
            timeSource = timeSource,
        )
        debugRepository.currentTimeMillis = {
            testScheduler.currentTime
        }
        debugRepository.debugCountdownStartMillis = -18_000

        val result = debugRepository.getTimeUntilNextDay(
            reduceTimeFlag = true,
        ).take(3).toList()

        assertEquals(
            listOf(
                "00:00:02" to false,
                "00:00:01" to true,
                "00:00:19" to false,
            ),
            result,
        )
        assertEquals(2, debugRepository.getDaysSinceStartCount().first())
        assertEquals(1, debugRepository.getDebugDaysOffset())
    }

    @Test
    fun `getDebugDaysOffset returns zero without debug advance`() = runTest {
        val now = LocalDate.of(2025, 7, 1).atStartOfDay()
        val millis = now.atZone(zone).toInstant().toEpochMilli()
        every { timeSource.getCurrentUnixEpochMillis() } returns flowOf(millis)

        coEvery { serverRepository.getGameCountdownStartDate() } returns "29.06.2025"

        assertEquals(0, repository.getDebugDaysOffset())
        assertEquals(2, repository.getDaysSinceStartCount().first())
    }
}