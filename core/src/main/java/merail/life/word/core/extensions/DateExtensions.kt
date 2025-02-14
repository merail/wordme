package merail.life.word.core.extensions

import java.util.Calendar
import java.util.Locale

fun getTimeUntilNextDay(): String {
    val calendar = Calendar.getInstance()

    val currentTimeMillis = calendar.timeInMillis

    calendar.add(Calendar.DAY_OF_YEAR, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val millisUntilNextDay = calendar.timeInMillis - currentTimeMillis

    val hours = millisUntilNextDay / (1000 * 60 * 60)
    val minutes = (millisUntilNextDay / (1000 * 60)) % 60
    val seconds = (millisUntilNextDay / 1000) % 60

    return String.format(
        locale = Locale("ru"),
        format = "%02d:%02d:%02d",
        hours,
        minutes,
        seconds,
    )
}