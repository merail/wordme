package merail.life.word.stats

import androidx.compose.runtime.Composable
import merail.life.word.stats.view.StatsScreen

@Composable
fun StatsContainer(
    onDismiss: () -> Unit,
) = StatsScreen(
    onDismiss = onDismiss,
)