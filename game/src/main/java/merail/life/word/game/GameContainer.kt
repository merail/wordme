package merail.life.word.game

import androidx.compose.runtime.Composable
import merail.life.word.game.view.GameScreen

@Composable
fun GameContainer(
    onVictory: () -> Unit,
) = GameScreen(
    onVictory = onVictory,
)