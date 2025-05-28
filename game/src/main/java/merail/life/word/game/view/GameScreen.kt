package merail.life.word.game.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import merail.life.word.core.extensions.isNavigationBarEnabled
import merail.life.word.game.GameViewModel
import merail.life.word.game.state.GameResultState

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
    onGameEnd: (isVictory: Boolean) -> Unit,
    onInfoClick: () -> Unit,
    viewModel: GameViewModel = hiltViewModel<GameViewModel>(),
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
    ) {
        var isResultBoardVisible = remember {
            mutableStateOf(false)
        }

        Toolbar(
            onInfoClick = onInfoClick,
        )

        KeyFields(
            keyForms = viewModel.keyForms,
            wordCheckState = viewModel.wordCheckState,
            onFlipAnimationEnd = remember {
                {
                    viewModel.setKeyButtonsStateAfterWordCheck()
                    when (viewModel.gameResultState.value) {
                        is GameResultState.Process -> viewModel.disableControlKeys()
                        is GameResultState.Victory -> {
                            onGameEnd(true)
                            isResultBoardVisible.value = true
                        }
                        is GameResultState.Defeat -> {
                            onGameEnd(false)
                            isResultBoardVisible.value = true
                        }
                    }
                }
            },
        )

        var keyboardHeight = remember {
            mutableIntStateOf(0)
        }

        if (isResultBoardVisible.value) {
            ResultBoard(
                viewModel = viewModel,
                keyboardHeight = keyboardHeight,
                onResultClick = remember {
                    {
                        onGameEnd(viewModel.gameResultState.value.isWin)
                    }
                },
            )
        } else {
            Keyboard(
                keyButtons = viewModel.keyButtons,
                checkWordKeyState = viewModel.checkWordKeyState,
                deleteKeyState = viewModel.deleteKeyState,
                keyboardHeight = keyboardHeight,
                onKeyButtonClick = remember {
                    {
                        viewModel.handleKeyClick(it)
                    }
                },
            )
        }
    }
}