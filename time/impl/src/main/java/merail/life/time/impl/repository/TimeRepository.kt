package merail.life.time.impl.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import merail.life.server.api.IServerRepository
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
import kotlin.time.Duration.Companion.milliseconds

internal val fakeStartTime = LocalDateTime.now().with(LocalTime.of(23, 59, 40))

internal class TimeRepository @Inject constructor(
    private val appScope: CoroutineScope,
    private val serverRepository: IServerRepository,
    private val timeSource: ITimeSource,
) : ITimeRepository {

    private val debugTimeUntilNextDay by lazy {
        flow {
            while (true) {
                val millisPassed = System.currentTimeMillis() - countdownStartRealTime
                val nowFake = fakeStartTime.plusNanos(millisPassed * 1_000_000)
                val midnight = fakeStartTime.toLocalDate().plusDays(1).atStartOfDay()

                emit(nowFake to Duration.between(nowFake, midnight).coerceAtLeast(Duration.ZERO))

                delay(1000L.milliseconds)
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

                emit(now to Duration.between(now, midnight).coerceAtLeast(Duration.ZERO))

                delay(1000L.milliseconds)
            }
        }.shareIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )
    }

    private val dotFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())

    private fun String.toLocalDate() = LocalDate.parse(this, dotFormatter)

    private suspend fun getGameCountdownStartDate() = serverRepository.getGameCountdownStartDate().toLocalDate()

    override fun getDaysSinceStartCount() = timeSource.getCurrentUnixEpochMillis().map {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
    }.map {
        val startDay = getGameCountdownStartDate().minusDays(debugDaysSinceStartCount.toLong())
        ChronoUnit.DAYS.between(startDay, it.plusSeconds(1)).coerceAtLeast(0).toInt()
    }

    override suspend fun getTimeUntilNextDay(
        reduceTimeFlag: Boolean,
    ) = flow {
        val timeFlow = if (reduceTimeFlag) {
            debugTimeUntilNextDay
        } else {
            releaseTimeUntilNextDay
        }

        var previousDay: LocalDate? = null

        timeFlow.collect { (now, timeUntilNextDay) ->
            val day = now.plusSeconds(1).toLocalDate()
            val isNextDay = previousDay?.let {
                day.isAfter(it)
            } == true
            previousDay = day

            val hours = timeUntilNextDay.toHours()
            val minutes = timeUntilNextDay.toMinutes() % 60
            val seconds = timeUntilNextDay.seconds % 60

            emit(
                String.format(
                    locale = Locale.getDefault(),
                    format = "%02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds,
                ) to isNextDay,
            )
        }
    }
}