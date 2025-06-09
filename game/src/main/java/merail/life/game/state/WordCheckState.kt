package merail.life.game.state

import androidx.compose.runtime.Stable

@Stable
internal sealed class WordCheckState(
    open val currentRow: Int?,
) {
    val isError: Boolean
        get() = this is NonExistentWord

    data object None: WordCheckState(null)

    data class NonExistentWord(
        override val currentRow: Int,
    ): WordCheckState(currentRow)

    data class ExistingWord(
        override val currentRow: Int,
    ): WordCheckState(currentRow)

    data class CorrectWord(
        override val currentRow: Int,
    ): WordCheckState(currentRow)
}