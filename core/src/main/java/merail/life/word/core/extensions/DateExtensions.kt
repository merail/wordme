package merail.life.word.core.extensions

import merail.life.word.core.BuildConfig
import java.util.Calendar
import java.util.Locale

fun getTimeUntilNextDay(): Pair<String, Boolean> {
    val millisUntilNextDay = if (BuildConfig.DEBUG) {
        getDebugTimeUntilNextDay()
    } else {
        getReleaseTimeUntilNextDay()
    }

    val hours = millisUntilNextDay / (1000 * 60 * 60)
    val minutes = (millisUntilNextDay / (1000 * 60)) % 60
    val seconds = (millisUntilNextDay / 1000) % 60

    val isNextDay = hours == 0L && minutes == 0L && seconds == 0L

    return String.format(
        locale = Locale("ru"),
        format = "%02d:%02d:%02d",
        hours,
        minutes,
        seconds,
    ) to isNextDay
}

private fun getReleaseTimeUntilNextDay(): Long {
    val calendar = Calendar.getInstance()

    val currentTimeMillis = calendar.timeInMillis

    calendar.add(Calendar.DAY_OF_YEAR, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis - currentTimeMillis
}

private val debugTimeUntilNextDay = Calendar.getInstance().apply {
    add(Calendar.SECOND, 20)
}.timeInMillis

@JvmName("functionOfKotlin")
private fun getDebugTimeUntilNextDay(): Long {
    val calendar = Calendar.getInstance()

    return debugTimeUntilNextDay - calendar.timeInMillis
}