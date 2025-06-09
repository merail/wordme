package merail.life.result

import androidx.compose.runtime.Composable
import merail.life.result.view.ResultScreen

@Composable
fun ResultContainer(
    onDismiss: () -> Unit,
) = ResultScreen(
    onDismiss = onDismiss,
)