package merail.life.stats

import androidx.compose.runtime.Composable
import merail.life.stats.view.StatsScreen

@Composable
fun StatsContainer(
    onDismiss: () -> Unit,
) = StatsScreen(
    onDismiss = onDismiss,
)