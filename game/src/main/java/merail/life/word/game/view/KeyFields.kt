package merail.life.word.game.view

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import merail.life.word.design.WordMeTheme
import merail.life.word.game.COLUMNS_COUNT
import merail.life.word.game.KEYBOARD_COLUMNS_COUNT
import merail.life.word.game.ROWS_COUNT
import merail.life.word.game.model.Key
import merail.life.word.game.model.KeyCell
import merail.life.word.game.model.KeyState
import merail.life.word.game.state.WordCheckState
import merail.life.word.game.utils.KeyCellsList
import merail.life.word.game.utils.emptyKeyField
import merail.life.word.game.utils.lastFilledRow
import merail.life.word.game.utils.launchBounceAnimation
import merail.life.word.game.utils.launchErrorColorAnimation
import merail.life.word.game.utils.launchFlipAnimation
import merail.life.word.game.utils.launchVibrateAnimation
import androidx.compose.animation.Animatable as ColorAnimatable

private const val KEY_SIZE_THRESHOLD = 60

@Composable
internal fun ColumnScope.KeyFields(
    keyForms: KeyCellsList,
    wordCheckState: MutableState<WordCheckState>,
    isNextDay: Boolean,
    onFlipAnimationEnd: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = keyFieldsVerticalPadding,
            )
            .weight(1f),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(keyFieldContentVerticalPadding),
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            repeat(ROWS_COUNT) { row ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = keyFieldHorizontalPadding,
                        ),
                ) {
                    repeat(COLUMNS_COUNT) { column ->
                        KeyForm(
                            scope = scope,
                            row = row,
                            column = column,
                            keyForm = keyForms[row][column],
                            wordCheckState = wordCheckState,
                            isLastFilledRow = row == keyForms.lastFilledRow,
                            isNextDay = isNextDay,
                            onFlipAnimationEnd = onFlipAnimationEnd,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyForm(
    scope: CoroutineScope,
    row: Int,
    column: Int,
    keyForm: KeyCell,
    wordCheckState: MutableState<WordCheckState>,
    isLastFilledRow: Boolean,
    isNextDay: Boolean,
    onFlipAnimationEnd: () -> Unit,
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val bottomPadding = LocalContext.current.bottomPadding

    val contentWidth = remember {
        (screenWidthDp - (keyFieldHorizontalPadding * 2 + (keyFieldContentHorizontalPadding * 2
                + keyFieldContentBorder) * COLUMNS_COUNT)) / COLUMNS_COUNT
    }

    val contentHeight = remember {
        (screenHeightDp - topPadding - toolbarMinHeight - keyFieldsVerticalPadding * 2 -
                keyFieldContentVerticalPadding * (ROWS_COUNT - 1) -
                keyboardContentHeight * KEYBOARD_COLUMNS_COUNT - keyboardContentVerticalPadding *
                (KEYBOARD_COLUMNS_COUNT - 1) - bottomPadding) / ROWS_COUNT
    }

    val contentSize = remember {
        contentWidth.coerceAtMost(contentHeight)
    }

    val bounceAnimation = remember {
        Animatable(0f)
    }

    val vibrateAnimation = remember {
        Animatable(0f)
    }

    val initialColor = WordMeTheme.colors.textInversePrimary
    val targetColor = WordMeTheme.colors.textNegative
    val animatableColor = remember {
        ColorAnimatable(initialColor)
    }

    val rotationYList = remember(isNextDay) {
        (1..COLUMNS_COUNT).map {
            Animatable(0f)
        }
    }

    LaunchedEffect(keyForm.key.value) {
        if (keyForm.key.value.isNotEmpty()) {
            scope.launchBounceAnimation(
                bounceAnimation = bounceAnimation,
            )
        }
    }

    LaunchedEffect(wordCheckState.value) {
        if (wordCheckState.value.isError && wordCheckState.value.currentRow == row) {
            scope.launchVibrateAnimation(
                vibrateAnimation =  vibrateAnimation,
            )
        }
    }

    LaunchedEffect(wordCheckState.value) {
        if (wordCheckState.value.isError && wordCheckState.value.currentRow == row) {
            scope.launchErrorColorAnimation(
                animatableColor = animatableColor,
                initialColor = initialColor,
                targetColor = targetColor,
            )
        }
    }

    LaunchedEffect(keyForm.state) {
        if (keyForm.state != KeyState.DEFAULT) {
            scope.launchFlipAnimation(
                column = column,
                rotationAnimation = rotationYList[column],
                isLastFilledRow = isLastFilledRow,
                onFlipAnimationEnd = onFlipAnimationEnd,
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = bounceAnimation.value.toInt(),
                )
            }
            .offset {
                IntOffset(
                    x = vibrateAnimation.value.toInt(),
                    y = 0,
                )
            }
            .graphicsLayer {
                rotationY = rotationYList[column].value
                cameraDistance = 8 * density
            }
            .size(contentSize)
            .padding(
                horizontal = keyFieldContentHorizontalPadding,
            )
            .border(
                width = keyFieldContentBorder,
                color = if (rotationYList[column].value <= 90f) {
                    WordMeTheme.colors.elementPositive
                } else {
                    Color.Unspecified
                },
                shape = RoundedCornerShape(4.dp),
            )
            .aspectRatio(1f)
            .background(
                color = if (rotationYList[column].value <= 90f) {
                    WordMeTheme.colors.elementPrimary
                } else {
                    when (keyForm.state) {
                        KeyState.CORRECT -> WordMeTheme.colors.elementPositive
                        KeyState.PRESENT -> WordMeTheme.colors.elementInversePrimary
                        else -> WordMeTheme.colors.elementSecondary
                    }
                },
                shape = RoundedCornerShape(4.dp),
            ),
    ) {
        Text(
            text = keyForm.key.value,
            color = if (rotationYList[column].value <= 90f) {
                animatableColor.value
            } else {
                when (keyForm.state) {
                    KeyState.CORRECT,
                    KeyState.PRESENT,
                    -> WordMeTheme.colors.textPrimary
                    else -> WordMeTheme.colors.textInversePrimary
                }
            },
            textAlign = TextAlign.Center,
            style = if (contentWidth < KEY_SIZE_THRESHOLD.dp) {
                WordMeTheme.typography.titleLarge
            } else {
                WordMeTheme.typography.displaySmall
            },
            modifier = Modifier
                .graphicsLayer {
                    rotationY = rotationYList[column].value
                    cameraDistance = 8 * density
                },
        )
    }
}

@Preview
@Composable
private fun KeyFieldsPreview() {
    Column {
        KeyFields(
            keyForms = remember {
                mutableStateListOf(
                    mutableStateListOf(KeyCell(Key.Б), KeyCell(Key.А), KeyCell(Key.Р), KeyCell(Key.А), KeyCell(Key.Н)),
                    emptyKeyField,
                    emptyKeyField,
                    emptyKeyField,
                    emptyKeyField,
                    emptyKeyField,
                )
            },
            wordCheckState = remember {
                mutableStateOf(WordCheckState.None)
            },
            isNextDay = false,
            onFlipAnimationEnd = {},
        )
    }
}