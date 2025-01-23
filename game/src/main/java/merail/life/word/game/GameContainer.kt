package merail.life.word.game

import androidx.compose.runtime.Composable
import merail.life.word.game.view.GameScreen

@Composable
fun GameContainer(
    onResult: (isVictory: Boolean) -> Unit,
    onStats: () -> Unit,
) = GameScreen(
    onResult = onResult,
    onStats = onStats,
)