package merail.life.time.impl

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import merail.life.config.api.IConfigRepository
import merail.life.time.api.ITimeRepository
import merail.life.time.api.ITimeSource
import merail.life.time.impl.repository.TimeRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.*

@OptIn(ExperimentalCoroutinesApi::class)
class TestTimeRepository {

    private lateinit var configRepository: IConfigRepository
    private lateinit var timeSource: ITimeSource
    private lateinit var repository: TimeRepository

    private val zone = ZoneId.systemDefault()

    @Before
    fun setUp() {
        configRepository = mockk()
        timeSource = mockk()
        repository = TimeRepository(configRepository, timeSource)
    }

    @Test
    fun `getDaysSinceStartCount returns correct number of days`() = runTest {
        val now = LocalDate.of(2025, 7, 1).atStartOfDay()
        val millis = now.atZone(zone).toInstant().toEpochMilli()
        every { timeSource.getCurrentUnixEpochMillis() } returns flowOf(millis)

        every { configRepository.getGameCountdownStartDate() } returns flowOf("29.06.2025")

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
        )

        assertEquals("00:00:02", timeString)
        assertEquals(false, isNextDay)
    }

    @Test
    fun `getTimeUntilNextDay uses debug logic`() = runTest {
        ITimeRepository.countdownStartRealTime = System.currentTimeMillis() - 20_000

        val fakeStart = LocalDateTime.now().with(LocalTime.of(23, 59, 40))
        mockkConstructor(TimeRepository::class)
        every { anyConstructed<TimeRepository>().fakeStartTime } returns fakeStart

        val (timeString, isNextDay) = repository.getTimeUntilNextDay(
            reduceTimeFlag = true,
        )

        assertEquals("00:00:00", timeString)
        assertEquals(true, isNextDay)
    }
}