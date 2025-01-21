package merail.life.word.result

import androidx.compose.runtime.Composable
import merail.life.word.result.view.ResultScreen

@Composable
fun ResultContainer(
    onDismiss: () -> Unit,
) = ResultScreen(
    onDismiss = onDismiss,
)