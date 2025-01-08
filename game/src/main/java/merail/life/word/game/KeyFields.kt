package merail.life.word.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import merail.life.word.design.WordMeTheme

@Composable
internal fun KeyFields(
    keyFields: SnapshotStateList<SnapshotStateList<Key>>,
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

    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            scope.launch {
                bounceAnimation.animateTo(
                    targetValue = -10f,
                    animationSpec = tween(
                        durationMillis = 80,
                    )
                )
                bounceAnimation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 80,
                    )
                )
            }
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
            .size(contentSize)
            .padding(
                horizontal = keyFieldContentHorizontalPadding,
            )
            .border(
                width = keyFieldContentBorder,
                color = WordMeTheme.colors.borderPrimary,
                shape = RoundedCornerShape(4.dp),
            )
            .aspectRatio(1f),
    ) {
        Text(
            text = value,
            color = WordMeTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            style = WordMeTheme.typography.titleLarge,
        )
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
    )
}