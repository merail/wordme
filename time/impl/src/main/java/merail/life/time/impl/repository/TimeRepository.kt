package merail.life.time.impl.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import merail.life.config.api.IConfigRepository
import merail.life.time.api.ITimeRepository
import merail.life.time.api.ITimeRepository.Companion.countdownStartRealTime
import merail.life.time.api.ITimeRepository.Companion.debugDaysSinceStartCount
import merail.life.time.api.ITimeSource
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject


internal val fakeStartTime = LocalDateTime.now().with(LocalTime.of(23, 59, 40))

internal class TimeRepository @Inject constructor(
    private val appScope: CoroutineScope,
    private val configRepository: IConfigRepository,
    private val timeSource: ITimeSource,
) : ITimeRepository {

    private val debugTimeUntilNextDay by lazy {
        flow<Duration> {
            while (true) {
                val millisPassed = System.currentTimeMillis() - countdownStartRealTime
                val nowFake = fakeStartTime.plusNanos(millisPassed * 1_000_000)
                val midnight = fakeStartTime.toLocalDate().plusDays(1).atStartOfDay()

                emit(Duration.between(nowFake, midnight).coerceAtLeast(Duration.ZERO))

                delay(1000L)
            }
        }.shareIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            replay = 1,
        )
    }

    private val releaseTimeUntilNextDay by lazy {
        flow {
            while (true) {
                val epochMillis = timeSource.getCurrentUnixEpochMillis().first()
                val now = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
                val midnight = now.toLocalDate().plusDays(1).atStartOfDay()

                emit(Duration.between(now, midnight).coerceAtLeast(Duration.ZERO))

                delay(1000L)
            }
        }.shareIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )
    }

    private val dotFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())

    private fun String.toLocalDate() = LocalDate.parse(this, dotFormatter)

    private val gameCountdownStartDate: Flow<LocalDate>
        get() = configRepository.getGameCountdownStartDate().map {
            it.toLocalDate()
        }

    override fun getDaysSinceStartCount() = timeSource.getCurrentUnixEpochMillis().map {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
    }.map {
        val startDay = gameCountdownStartDate.first().minusDays(debugDaysSinceStartCount.toLong())
        ChronoUnit.DAYS.between(startDay, it.plusSeconds(1)).coerceAtLeast(0).toInt()
    }

    override suspend fun getTimeUntilNextDay(
        reduceTimeFlag: Boolean,
    ) = if (reduceTimeFlag) {
        debugTimeUntilNextDay
    } else {
        releaseTimeUntilNextDay
    }.map {
        val hours = it.toHours()
        val minutes = it.toMinutes() % 60
        val seconds = it.seconds % 60

        val isNextDay = hours == 0L && minutes == 0L && seconds == 0L

        String.format(
            locale = Locale.getDefault(),
            format = "%02d:%02d:%02d",
            hours,
            minutes,
            seconds,
        ) to isNextDay
    }
}