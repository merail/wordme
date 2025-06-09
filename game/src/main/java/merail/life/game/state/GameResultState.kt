package merail.life.game.state

import androidx.compose.runtime.Stable

@Stable
internal sealed class GameResultState {
    val isGameEnd: Boolean
        get() = this is Defeat || this is Victory

    val isWin: Boolean
        get() = this is Victory

    data object Process: GameResultState()

    data object Defeat: GameResultState()

    data object Victory : GameResultState()
}