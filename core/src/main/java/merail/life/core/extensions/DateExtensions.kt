package merail.life.core.extensions

import merail.life.core.BuildConfig
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Locale

fun getDaysSinceStartCount(): Int {
    val startDay = LocalDate.of(2025, 6, 11 - debugDaysSinceStartCount)
    val today = LocalDate.now()
    return ChronoUnit.DAYS.between(startDay, today).toInt()
}

fun getTimeUntilNextDay(): Pair<String, Boolean> {
    val durationUntilNextDay = if (BuildConfig.REDUCE_TIME_UNTIL_NEXT_DAY) {
        getDebugTimeUntilNextDay()
    } else {
        getReleaseTimeUntilNextDay()
    }

    val hours = durationUntilNextDay.toHours()
    val minutes = durationUntilNextDay.toMinutes() % 60
    val seconds = durationUntilNextDay.seconds % 60

    val isNextDay = hours == 0L && minutes == 0L && seconds == 0L

    return String.format(
        locale = Locale("ru"),
        format = "%02d:%02d:%02d",
        hours,
        minutes,
        seconds,
    ) to isNextDay
}

private fun getReleaseTimeUntilNextDay(): Duration {
    val now = LocalDateTime.now()
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
    return Duration.between(now, midnight).coerceAtLeast(Duration.ZERO)
}

var countdownStartRealTime = System.currentTimeMillis()
var debugDaysSinceStartCount = 0
private val fakeStartTime = LocalDateTime.now().with(LocalTime.of(23, 59, 40))

private fun getDebugTimeUntilNextDay(): Duration {
    val millisPassed = System.currentTimeMillis() - countdownStartRealTime
    val nowFake = fakeStartTime.plusNanos(millisPassed * 1_000_000)
    val midnight = fakeStartTime.toLocalDate().plusDays(1).atStartOfDay()

    return Duration.between(nowFake, midnight).coerceAtLeast(Duration.ZERO)
}