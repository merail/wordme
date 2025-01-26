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
import merail.life.word.game.model.KeyCellState
import merail.life.word.game.model.KeyCellsList
import merail.life.word.game.model.emptyKeyField
import merail.life.word.game.model.lastFilledRow
import merail.life.word.game.state.WordCheckState
import androidx.compose.animation.Animatable as ColorAnimatable

@Composable
internal fun ColumnScope.KeyFields(
    keyFields: KeyCellsList,
    wordCheckState: MutableState<WordCheckState>,
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
                        KeyCell(
                            scope = scope,
                            row = row,
                            column = column,
                            wordCheckState = wordCheckState,
                            keyCell = keyFields[row][column],
                            isLastFilledRow = row == keyFields.lastFilledRow,
                            onFlipAnimationEnd = onFlipAnimationEnd,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyCell(
    scope: CoroutineScope,
    row: Int,
    column: Int,
    wordCheckState: MutableState<WordCheckState>,
    keyCell: KeyCell,
    isLastFilledRow: Boolean,
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

    val contentSize = contentWidth.coerceAtMost(contentHeight)

    val bounceAnimation = remember {
        Animatable(0f)
    }

    val vibrateAnimation = remember {
        Animatable(0f)
    }

    val initialColor = WordMeTheme.colors.textPrimary
    val targetColor = WordMeTheme.colors.textNegative
    val animatableColor = remember {
        ColorAnimatable(initialColor)
    }

    val rotationYList = remember {
        (1..COLUMNS_COUNT).map {
            Animatable(0f)
        }
    }

    LaunchedEffect(keyCell.key.value) {
        if (keyCell.key.value.isNotEmpty()) {
            scope.launchBounceAnimation(
                bounceAnimation = bounceAnimation,
            )
        }
    }

    LaunchedEffect(wordCheckState) {
        if (wordCheckState.value.isError && wordCheckState.value.currentRow == row) {
            scope.launchVibrateAnimation(
                vibrateAnimation =  vibrateAnimation,
            )
        }
    }

    LaunchedEffect(wordCheckState) {
        if (wordCheckState.value.isError && wordCheckState.value.currentRow == row) {
            scope.launchErrorColorAnimation(
                animatableColor = animatableColor,
                initialColor = initialColor,
                targetColor = targetColor,
            )
        }
    }

    LaunchedEffect(keyCell.state) {
        if (keyCell.state != KeyCellState.DEFAULT) {
            scope.launchFlipAnimation(
                column = column,
                rotationAnimation = rotationYList[column],
                isLastFilledRow = isLastFilledRow,
                onAnimationEnd = onFlipAnimationEnd,
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
                color = WordMeTheme.colors.keyCellBorder,
                shape = RoundedCornerShape(4.dp),
            )
            .aspectRatio(1f)
            .background(
                color = if (rotationYList[column].value <= 90f) {
                    WordMeTheme.colors.keyCellBackgroundDefault
                } else {
                    when (keyCell.state) {
                        KeyCellState.CORRECT -> WordMeTheme.colors.keyCellBackgroundCorrect
                        KeyCellState.PRESENT -> WordMeTheme.colors.keyCellBackgroundPresent
                        else -> WordMeTheme.colors.keyCellBackgroundAbsent
                    }
                },
                shape = RoundedCornerShape(4.dp),
            ),
    ) {
        Text(
            text = keyCell.key.value,
            color = animatableColor.value,
            textAlign = TextAlign.Center,
            style = WordMeTheme.typography.titleLarge,
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
            keyFields = remember {
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
            onFlipAnimationEnd = {},
        )
    }
}