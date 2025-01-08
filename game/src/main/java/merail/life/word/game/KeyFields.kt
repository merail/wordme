package merail.life.word.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.snapshots.SnapshotStateList
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

@Composable
internal fun KeyFields(
    keyFields: SnapshotStateList<SnapshotStateList<Key>>,
    wordCheckState: MutableState<WordCheckState>,
) {
    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(keyFieldContentVerticalPadding),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = keyFieldsBottomPadding,
            ),
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
                        wordCheckState = wordCheckState.value,
                        value = keyFields[row][column].value,
                    )
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
    value: String,
) {
    val contentWidth = (LocalConfiguration.current.screenWidthDp.dp -
            (keyFieldHorizontalPadding * 2 + (keyFieldContentHorizontalPadding * 2
                    + keyFieldContentBorder) * COLUMNS_COUNT)) / COLUMNS_COUNT

    val contentHeight = (LocalConfiguration.current.screenHeightDp.dp -
            toolbarHeight - keyFieldContentVerticalPadding * (ROWS_COUNT - 1) -
            keyboardContentHeight * KEYBOARD_COLUMNS_COUNT - keyboardContentVerticalPadding *
            (KEYBOARD_COLUMNS_COUNT - 1) - keyFieldsBottomPadding -
            LocalContext.current.bottomPadding) / ROWS_COUNT

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

    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            scope.launchBounceAnimation(
                bounceAnimation = bounceAnimation,
            )
        }
    }

    LaunchedEffect(wordCheckState) {
        if (wordCheckState is WordCheckState.NonExistentWord && wordCheckState.currentRow == row) {
            scope.launchVibrateAnimation(
                vibrateAnimation =  vibrateAnimation,
            )
        }
    }

    LaunchedEffect(wordCheckState) {
        if (wordCheckState is WordCheckState.NonExistentWord && wordCheckState.currentRow == row) {
            scope.launchErrorColorAnimation(
                animatableColor = animatableColor,
                initialColor = initialColor,
                targetColor = Color.Red,
            )
        }
    }

    LaunchedEffect(wordCheckState) {
        if (wordCheckState is WordCheckState.ExistingWord && wordCheckState.currentRow == row) {
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
                    WordMeTheme.colors.elementBackgroundSecondary
                },
                shape = RoundedCornerShape(4.dp),
            ),
    ) {
        Text(
            text = value,
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
                delay(index * 300L)
                animatable.animateTo(
                    targetValue = 180f,
                    animationSpec = tween(
                        durationMillis = 600,
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun KeyFieldsPreview() {
    KeyFields(
        keyFields = remember {
            mutableStateListOf(
                mutableStateListOf(Key.Б, Key.А, Key.Р, Key.А, Key.Н),
                mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
                mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
                mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
                mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
                mutableStateListOf(Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY, Key.EMPTY),
            )
        },
        wordCheckState = remember {
            mutableStateOf(WordCheckState.None)
        },
    )
}