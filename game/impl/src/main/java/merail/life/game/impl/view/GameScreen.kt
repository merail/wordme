package merail.life.game.impl.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import merail.life.core.extensions.isNavigationBarEnabled
import merail.life.game.impl.GameViewModel
import merail.life.game.impl.state.isGettingError

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
    onGameEnd: (isVictory: Boolean, attemptsCount: Int) -> Unit,
    onInfoClick: () -> Unit,
    viewModel: GameViewModel = hiltViewModel<GameViewModel>(),
) {
    val keyForms by viewModel.keyForms.collectAsState()
    val keyButtons by viewModel.keyButtons.collectAsState()
    val wordCheckState by viewModel.wordCheckState.collectAsState()
    val isNextDay by viewModel.isNextDay.collectAsState()
    val isResultBoardVisible by viewModel.isResultBoardVisible.collectAsState()
    val gameResultState by viewModel.gameResultState.collectAsState()
    val checkWordKeyState by viewModel.checkWordKeyState.collectAsState()
    val deleteKeyState by viewModel.deleteKeyState.collectAsState()
    val timeUntilNextDay by viewModel.timeUntilNextDay.collectAsState()
    val gameErrorState by viewModel.gameErrorState.collectAsState()

    val keyboardHeight = remember {
        mutableIntStateOf(0)
    }

    val keyFieldsContentBottom = remember {
        mutableIntStateOf(0)
    }

    val parentBottom = remember {
        mutableIntStateOf(0)
    }

    Box(
        modifier = Modifier
            .onGloballyPositioned {
                parentBottom.intValue = (it.positionInRoot().y + it.size.height).toInt()
            },
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
        ) {
            Toolbar(
                onInfoClick = onInfoClick,
            )

            KeyFields(
                keyForms = keyForms,
                wordCheckState = wordCheckState,
                isNextDay = isNextDay,
                keyFieldsContentBottom = keyFieldsContentBottom,
                onFlipAnimationEnd = {
                    viewModel.onFlipAnimationEnd(onGameEnd)
                },
            )

            if (isResultBoardVisible) {
                val density = LocalDensity.current
                val bottomPadding = LocalContext.current.bottomPadding
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            height = with(density) {
                                keyboardHeight.intValue.toDp()
                            } + bottomPadding,
                        ),
                )
            } else {
                Keyboard(
                    keyButtons = keyButtons,
                    checkWordKeyState = checkWordKeyState,
                    deleteKeyState = deleteKeyState,
                    keyboardHeight = keyboardHeight,
                    onKeyButtonClick = {
                        if (gameErrorState.isGettingError.not()) {
                            viewModel.handleKeyClick(it)
                        }
                    },
                )
            }
        }

        if (isResultBoardVisible) {
            val density = LocalDensity.current
            val availableHeight = parentBottom.intValue - keyFieldsContentBottom.intValue
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(
                        height = with(density) {
                            availableHeight.toDp()
                        },
                    ),
            ) {
                ResultBoard(
                    timeUntilNextDay = timeUntilNextDay,
                    onResultClick = {
                        onGameEnd(
                            gameResultState.isWin,
                            viewModel.currentIndex.first,
                        )
                    },
                )
            }
        }

        ErrorBanner(
            errorState = gameErrorState,
            onDismiss = viewModel::dismissError,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
        )
    }
}