package merail.life.word.game.state

internal sealed class WordCheckState {
    data object None: WordCheckState()

    data class ExistingWord(
        val currentRow: Int,
    ): WordCheckState()

    data class NonExistentWord(
        val currentRow: Int,
    ): WordCheckState()
}