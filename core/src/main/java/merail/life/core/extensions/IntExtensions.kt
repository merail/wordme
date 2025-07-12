package merail.life.core.extensions

import java.util.Locale

infix fun Int.percentOf(total: Int): String {
    val percentage = if (total == 0) {
        0.0
    } else {
        (toDouble() / total) * 100
    }
    return String.format(
        locale = Locale("ru"),
        format = "%.0f%%",
        percentage,
    )
}

infix fun Int.partOf(total: Int): String {
    val percentage = if (total == 0) {
        0.0
    } else {
        toDouble() / total
    }
    return String.format(
        locale = Locale.US,
        format = "%.1f",
        percentage,
    )
}