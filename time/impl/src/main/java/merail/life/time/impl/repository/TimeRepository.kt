package merail.life.time.impl.repository

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import merail.life.config.api.IConfigRepository
import merail.life.time.api.ITimeRepository
import merail.life.time.api.ITimeRepository.Companion.countdownStartRealTime
import merail.life.time.api.ITimeRepository.Companion.debugDaysSinceStartCount
import merail.life.time.api.ITimeSource
import merail.life.time.impl.BuildConfig
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

internal class TimeRepository @Inject constructor(
    private val configRepository: IConfigRepository,
    private val timeSource: ITimeSource,
) : ITimeRepository {

    private val currentLocalDateTime: Flow<LocalDateTime?>
        get() = timeSource.getCurrentUnixEpochMillis().map {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }

    private val dotFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())

    private fun String.toLocalDate() = LocalDate.parse(this, dotFormatter)

    private val gameCountdownStartDate: Flow<LocalDate>
        get() = configRepository.getGameCountdownStartDate().map {
            it.toLocalDate()
        }

    @VisibleForTesting
    val fakeStartTime = LocalDateTime.now().with(LocalTime.of(23, 59, 40))

    override fun getDaysSinceStartCount() = currentLocalDateTime.filterNotNull().map {
        val startDay = gameCountdownStartDate.first().minusDays(debugDaysSinceStartCount.toLong())
        ChronoUnit.DAYS.between(startDay, it).coerceAtLeast(0).toInt()
    }

    override suspend fun getTimeUntilNextDay(
        explicitReduceTimeFlag: Boolean,
    ) = if (BuildConfig.REDUCE_TIME_UNTIL_NEXT_DAY || explicitReduceTimeFlag) {
        getDebugTimeUntilNextDay()
    } else {
        getReleaseTimeUntilNextDay()
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
    }.first()

    private fun getReleaseTimeUntilNextDay() = currentLocalDateTime.filterNotNull().map {
        val midnight = it.toLocalDate().plusDays(1).atStartOfDay()

        Duration.between(it, midnight).coerceAtLeast(Duration.ZERO)
    }

    private fun getDebugTimeUntilNextDay() = flow<Duration> {
        val millisPassed = System.currentTimeMillis() - countdownStartRealTime
        val nowFake = fakeStartTime.plusNanos(millisPassed * 1_000_000)
        val midnight = fakeStartTime.toLocalDate().plusDays(1).atStartOfDay()

        emit(Duration.between(nowFake, midnight).coerceAtLeast(Duration.ZERO))
    }
}