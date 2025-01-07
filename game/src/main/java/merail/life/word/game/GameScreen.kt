package merail.life.word.game

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import merail.life.word.core.extensions.isNavigationBarEnabled
import merail.life.word.design.WordMeTheme

private val toolbarHeight = 32.dp

private val keyFieldHorizontalPadding = 8.dp
private val keyFieldContentHorizontalPadding = 4.dp
private val keyFieldContentVerticalPadding = 4.dp
private val keyFieldContentBorder = 1.dp
private val keyFieldsBottomPadding = 32.dp

private val keyboardRowHorizontalPadding = 6.dp
private val keyboardContentHorizontalPadding = 2.dp
private val keyboardContentVerticalPadding = 16.dp
private val keyboardContentBorder = 1.dp
private val keyboardContentHeight = 40.dp

private val Context.bottomPadding: Dp
    get() = if (isNavigationBarEnabled) {
        56.dp
    } else {
        24.dp
    }

@Composable
fun GameContainer() = GameScreen()

@Composable
internal fun GameScreen(
    viewModel: GameViewModel = hiltViewModel<GameViewModel>(),
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(toolbarHeight),
        )

        KeyFields(viewModel.keyFields)

        Keyboard(
            onKeyClick = viewModel::handleKeyClick,
        )
    }
}

@Composable
private fun KeyFields(
    keyFields:SnapshotStateList<SnapshotStateList<Key>>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(keyFieldContentVerticalPadding),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = keyFieldsBottomPadding,
            ),
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
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
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
                            text = keyFields[row][column].value,
                            color = WordMeTheme.colors.textPrimary,
                            textAlign = TextAlign.Center,
                            style = WordMeTheme.typography.titleLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Keyboard(
    onKeyClick: (Key) -> Unit,
) {
    val keyboardLayout = listOf(
        listOf(Key.А, Key.Б, Key.В, Key.Г, Key.Д, Key.Е, Key.Ж, Key.З, Key.И, Key.Й, Key.К, Key.Л),
        listOf(Key.М, Key.Н, Key.О, Key.П, Key.Р, Key.С, Key.Т, Key.У, Key.Ф, Key.Х, Key.Ц),
        listOf(Key.DEL, Key.Ч, Key.Ш, Key.Щ, Key.Ъ, Key.Ы, Key.Ь, Key.Э, Key.Ю, Key.Я, Key.OK),
    )

    val contentWidth = (LocalConfiguration.current.screenWidthDp.dp -
            (keyboardRowHorizontalPadding * 2 +
                    (keyboardContentHorizontalPadding * 2 + keyboardContentBorder) * 12)) / 12
    val controlKeyAdditionalWidth = 8.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(keyboardContentVerticalPadding),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = LocalContext.current.bottomPadding,
            ),
    ) {
        keyboardLayout.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = keyboardRowHorizontalPadding,
                    ),
            ) {
                row.forEach { key ->
                    Button(
                        onClick = remember {
                            {
                                onKeyClick(key)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (key.isControlKey) {
                                WordMeTheme.colors.elementSecondary
                            } else {
                                WordMeTheme.colors.elementBackground
                            },
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .padding(
                                horizontal = keyboardContentHorizontalPadding,
                            )
                            .border(
                                width = keyboardContentBorder,
                                color = WordMeTheme.colors.borderPrimary,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .height(
                                height = if (key.isControlKey) {
                                    contentWidth + controlKeyAdditionalWidth
                                    keyboardContentHeight
                                } else {
                                    keyboardContentHeight
                                },
                            )
                            .width(
                                width = if (key.isControlKey) {
                                    contentWidth + controlKeyAdditionalWidth
                                    keyboardContentHeight
                                } else {
                                    contentWidth
                                },
                            )
                            .align(Alignment.CenterVertically),
                    ) {
                        when (key) {
                            Key.DEL -> Image(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_delete_key),
                                contentDescription = null,
                            )
                            Key.OK -> Image(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_check_word),
                                contentDescription = null,
                            )
                            else -> Text(
                                text = key.value,
                                color = WordMeTheme.colors.textPrimary,
                                textAlign = TextAlign.Center,
                                style = WordMeTheme.typography.labelLarge,
                            )
                        }
                    }
                }
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
    )
}

@Preview
@Composable
private fun KeyboardPreview() {
    Keyboard(
        onKeyClick = {},
    )
}