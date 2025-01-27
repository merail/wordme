package merail.life.word.game.model

import androidx.compose.runtime.Immutable

@Immutable
internal data class KeyCell(
    val key: Key,
    val state: KeyState = KeyState.DEFAULT,
)