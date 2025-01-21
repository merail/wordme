package merail.life.word.victory

import androidx.compose.runtime.Composable
import merail.life.word.victory.view.VictoryScreen

@Composable
fun VictoryContainer(
    onDismiss: () -> Unit,
) = VictoryScreen(
    onDismiss = onDismiss,
)