package merail.life.game.impl.state

import androidx.compose.runtime.Stable

@Stable
internal sealed class GameErrorState {
    data object Hidden : GameErrorState()

    data object WordExistingCheckError : GameErrorState()

    data object DayWordGettingError : GameErrorState()

    data class Default(val message: String?) : GameErrorState()
}

internal val GameErrorState.isVisible: Boolean
    get() = this !is GameErrorState.Hidden

internal val GameErrorState.isGettingError: Boolean
    get() = this is GameErrorState.DayWordGettingError
