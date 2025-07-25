package merail.life.game.impl.state

import androidx.compose.runtime.Stable

@Stable
internal sealed class CheckWordKeyState {
    data object Disabled: CheckWordKeyState()

    data object Loading: CheckWordKeyState()

    data object Enabled : CheckWordKeyState()
}