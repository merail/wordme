package merail.life.word.game.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import merail.life.word.core.extensions.isNavigationBarEnabled
import merail.life.word.game.GameViewModel

internal val toolbarMinHeight = 32.dp

internal val keyFieldsVerticalPadding = 4.dp
internal val keyFieldHorizontalPadding = 8.dp
internal val keyFieldContentHorizontalPadding = 4.dp
internal val keyFieldContentVerticalPadding = 4.dp
internal val keyFieldContentBorder = 1.dp

internal val keyboardRowHorizontalPadding = 6.dp
internal val keyboardContentHorizontalPadding = 2.dp
internal val keyboardContentVerticalPadding = 16.dp
internal val keyboardContentBorder = 1.dp
internal val keyboardContentHeight = 40.dp

internal val topPadding = 56.dp
internal val Context.bottomPadding: Dp
    get() = if (isNavigationBarEnabled) {
        56.dp
    } else {
        24.dp
    }

@Composable
internal fun GameScreen(
    viewModel: GameViewModel = hiltViewModel<GameViewModel>(),
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
    ) {
        Toolbar()

        KeyFields(
            keyFields = viewModel.keyFields,
            wordCheckState = viewModel.wordCheckState,
        )

        Keyboard(
            onKeyClick = viewModel::handleKeyClick,
        )
    }
}