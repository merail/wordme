package merail.life.game.state

import androidx.compose.runtime.Stable

@Stable
internal sealed class DeleteKeyState {
    data object Disabled: DeleteKeyState()

    data object Enabled : DeleteKeyState()
}