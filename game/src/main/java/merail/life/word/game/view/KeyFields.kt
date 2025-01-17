package merail.life.word.game.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import merail.life.word.design.WordMeTheme
import merail.life.word.game.COLUMNS_COUNT
import merail.life.word.game.KEYBOARD_COLUMNS_COUNT
import merail.life.word.game.ROWS_COUNT
import merail.life.word.game.state.Key
import merail.life.word.game.state.KeyCell
import merail.life.word.game.state.KeyCellState
import merail.life.word.game.state.KeyCellsList
import merail.life.word.game.state.WordCheckState
import androidx.compose.animation.Animatable as ColorAnimatable

private const val BOUNCE_ANIMATION_TOP_VALUE = -10f
private const val BOUNCE_ANIMATION_BOTTOM_VALUE = 0f
private const val BOUNCE_ANIMATION_DURATION = 80

private const val VIBRATE_ANIMATION_START_VALUE = -10f
private const val VIBRATE_ANIMATION_END_VALUE = 10f
private const val VIBRATE_ANIMATION_DURATION = 40

private const val ERROR_COLOR_ANIMATION_DURATION = 10
private const val ERROR_COLOR_ANIMATION_DELAY = 500

private const val FLIP_ANIMATION_DELAY = 300L
private const val FLIP_ANIMATION_TARGET_VALUE = 180f
private const val FLIP_ANIMATION_DURATION = 600

@Composable
internal fun ColumnScope.KeyFields(
    keyFields: KeyCellsList,
    wordCheckState: WordCheckState,
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
    wordCheckState: WordCheckState,
    keyCell: KeyCell,
) {
    val contentWidth = (LocalConfiguration.current.screenWidthDp.dp -
            (keyFieldHorizontalPadding * 2 + (keyFieldContentHorizontalPadding * 2
                    + keyFieldContentBorder) * COLUMNS_COUNT)) / COLUMNS_COUNT

    val contentHeight = (LocalConfiguration.current.screenHeightDp.dp - topPadding - toolbarMinHeight -
            keyFieldsVerticalPadding * 2 - keyFieldContentVerticalPadding * (ROWS_COUNT - 1) -
            keyboardContentHeight * KEYBOARD_COLUMNS_COUNT - keyboardContentVerticalPadding *
            (KEYBOARD_COLUMNS_COUNT - 1) - LocalContext.current.bottomPadding) / ROWS_COUNT

    val contentSize = contentWidth.coerceAtMost(contentHeight)

    val bounceAnimation = remember {
        Animatable(0f)
    }

    val vibrateAnimation = remember {
        Animatable(0f)
    }

    val initialColor = WordMeTheme.colors.textPrimary
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
        if (wordCheckState.isError && wordCheckState.currentRow == row) {
            scope.launchVibrateAnimation(
                vibrateAnimation =  vibrateAnimation,
            )
        }
    }

    LaunchedEffect(wordCheckState) {
        if (wordCheckState.isError && wordCheckState.currentRow == row) {
            scope.launchErrorColorAnimation(
                animatableColor = animatableColor,
                initialColor = initialColor,
                targetColor = Color.Red,
            )
        }
    }

    LaunchedEffect(keyCell.state) {
        if (keyCell.state != KeyCellState.DEFAULT) {
            scope.launchFlipAnimation(
                rotationYList = rotationYList,
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
                this.rotationY = rotationYList[column].value
                cameraDistance = 8 * density
            }
            .size(contentSize)
            .padding(
                horizontal = keyFieldContentHorizontalPadding,
            )
            .border(
                width = keyFieldContentBorder,
                color = WordMeTheme.colors.borderPrimary,
                shape = RoundedCornerShape(4.dp),
            )
            .aspectRatio(1f)
            .background(
                color = if (rotationYList[column].value <= 90f) {
                    WordMeTheme.colors.elementBackgroundPrimary
                } else {
                    when (keyCell.state) {
                        KeyCellState.CORRECT -> WordMeTheme.colors.elementBackgroundPositive
                        KeyCellState.PRESENT -> WordMeTheme.colors.elementBackgroundInverseSecondary
                        else -> WordMeTheme.colors.elementBackgroundSecondary
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
                    this.rotationY = rotationYList[column].value
                    cameraDistance = 8 * density
                },
        )
    }
}

private fun CoroutineScope.launchBounceAnimation(
    bounceAnimation: Animatable<Float, AnimationVector1D>,
) {
    launch {
        bounceAnimation.animateTo(
            targetValue = BOUNCE_ANIMATION_TOP_VALUE,
            animationSpec = tween(
                durationMillis = BOUNCE_ANIMATION_DURATION,
            )
        )
        bounceAnimation.animateTo(
            targetValue = BOUNCE_ANIMATION_BOTTOM_VALUE,
            animationSpec = tween(
                durationMillis = BOUNCE_ANIMATION_DURATION,
            )
        )
    }
}

private fun CoroutineScope.launchVibrateAnimation(
    vibrateAnimation: Animatable<Float, AnimationVector1D>,
) {
    launch {
        repeat(4) {
            vibrateAnimation.animateTo(
                targetValue = VIBRATE_ANIMATION_END_VALUE,
                animationSpec = tween(
                    durationMillis = VIBRATE_ANIMATION_DURATION,
                )
            )
            vibrateAnimation.animateTo(
                targetValue = VIBRATE_ANIMATION_START_VALUE,
                animationSpec = tween(
                    durationMillis = VIBRATE_ANIMATION_DURATION,
                )
            )
        }
        vibrateAnimation.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = VIBRATE_ANIMATION_DURATION,
            )
        )
    }
}

private fun CoroutineScope.launchErrorColorAnimation(
    animatableColor: Animatable<Color, AnimationVector4D>,
    initialColor: Color,
    targetColor: Color,
) {
    launch {
        animatableColor.animateTo(
            targetValue = targetColor,
            animationSpec = tween(
                durationMillis = ERROR_COLOR_ANIMATION_DURATION,
            ),
        )
        animatableColor.animateTo(
            targetValue = initialColor,
            animationSpec = tween(
                delayMillis = ERROR_COLOR_ANIMATION_DELAY,
                durationMillis = ERROR_COLOR_ANIMATION_DURATION,
            ),
        )
    }
}

private fun CoroutineScope.launchFlipAnimation(
    rotationYList: List<Animatable<Float, AnimationVector1D>>,
) {
    launch {
        rotationYList.forEachIndexed { index, animatable ->
            launch {
                delay(index * FLIP_ANIMATION_DELAY)
                animatable.animateTo(
                    targetValue = FLIP_ANIMATION_TARGET_VALUE,
                    animationSpec = tween(
                        durationMillis = FLIP_ANIMATION_DURATION,
                    )
                )
            }
        }
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
                    mutableStateListOf(KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY)),
                    mutableStateListOf(KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY)),
                    mutableStateListOf(KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY)),
                    mutableStateListOf(KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY)),
                    mutableStateListOf(KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY), KeyCell(Key.EMPTY)),
                )
            },
            wordCheckState = WordCheckState.None,
        )
    }
}