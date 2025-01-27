package merail.life.word.game.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import merail.life.word.design.WordMeTheme
import merail.life.word.game.R
import merail.life.word.game.model.Key
import merail.life.word.game.model.KeyCell
import merail.life.word.game.model.KeyState
import merail.life.word.game.state.CheckWordKeyState
import merail.life.word.game.state.DeleteKeyState
import merail.life.word.game.utils.KeyCellsList
import merail.life.word.game.utils.defaultKeyButtons
import merail.life.word.game.utils.isControlKey

@Composable
internal fun Keyboard(
    keyButtons: KeyCellsList,
    checkWordKeyState: MutableState<CheckWordKeyState>,
    deleteKeyState: MutableState<DeleteKeyState>,
    onKeyButtonClick: (Key) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(keyboardContentVerticalPadding),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = LocalContext.current.bottomPadding,
            ),
    ) {
        keyButtons.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = keyboardRowHorizontalPadding,
                    ),
            ) {
                row.forEach { keyButton ->
                    KeyButton(
                        keyButton = keyButton,
                        checkWordKeyState = checkWordKeyState,
                        deleteKeyState = deleteKeyState,
                        onKeyClick = onKeyButtonClick,
                    )
                }
            }
        }
    }
}

@Composable
internal fun RowScope.KeyButton(
    keyButton: KeyCell,
    checkWordKeyState: MutableState<CheckWordKeyState>,
    deleteKeyState: MutableState<DeleteKeyState>,
    onKeyClick: (Key) -> Unit,
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val contentWidth = remember {
        (screenWidthDp - (keyboardRowHorizontalPadding * 2 +
                (keyboardContentHorizontalPadding * 2 + keyboardContentBorder) * 12)) / 12
    }

    Button(
        onClick = remember {
            {
                onKeyClick(keyButton.key)
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = when(keyButton.key) {
                Key.OK -> when (checkWordKeyState.value) {
                    is CheckWordKeyState.Disabled -> WordMeTheme.colors.controlKeyBackgroundDisabled
                    is CheckWordKeyState.Loading,
                    is CheckWordKeyState.Enabled,
                        -> WordMeTheme.colors.okControlKeyBackgroundEnabled
                }
                Key.DEL -> if (deleteKeyState.value is DeleteKeyState.Enabled) {
                    WordMeTheme.colors.delControlKeyBackgroundEnabled
                } else {
                    WordMeTheme.colors.controlKeyBackgroundDisabled
                }
                else -> when (keyButton.state) {
                    KeyState.DEFAULT -> WordMeTheme.colors.keyBackgroundDefault
                    KeyState.ABSENT -> WordMeTheme.colors.keyBackgroundAbsent
                    KeyState.PRESENT -> WordMeTheme.colors.keyCellBackgroundPresent
                    KeyState.CORRECT -> WordMeTheme.colors.keyCellBackgroundCorrect
                }
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
                color = when {
                    keyButton.isControlKey.not() && keyButton.state == KeyState.DEFAULT
                        -> WordMeTheme.colors.keyBorder
                    else -> Color.Unspecified
                },
                shape = RoundedCornerShape(4.dp),
            )
            .height(keyboardContentHeight)
            .width(
                width = if (keyButton.isControlKey) {
                    keyboardContentHeight
                } else {
                    contentWidth
                },
            )
            .align(Alignment.CenterVertically),
    ) {
        when (keyButton.key) {
            Key.DEL -> Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_delete_key),
                colorFilter = ColorFilter.tint(
                    color = if (deleteKeyState.value is DeleteKeyState.Enabled) {
                        WordMeTheme.colors.controlKeyContentEnabled
                    } else {
                        WordMeTheme.colors.controlKeyContentDisabled
                    }
                ),
                contentDescription = null,
            )
            Key.OK -> if (checkWordKeyState.value is CheckWordKeyState.Loading) {
                CircularProgressIndicator(
                    strokeWidth = 1.dp,
                    color = WordMeTheme.colors.controlKeyContentEnabled,
                    modifier = Modifier
                        .size(16.dp),
                )
            } else {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check_word),
                    colorFilter = ColorFilter.tint(
                        color = if (checkWordKeyState.value is CheckWordKeyState.Enabled) {
                            WordMeTheme.colors.controlKeyContentEnabled
                        } else {
                            WordMeTheme.colors.controlKeyContentDisabled
                        },
                    ),
                    contentDescription = null,
                )
            }
            else -> Text(
                text = keyButton.key.value,
                color = WordMeTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                style = WordMeTheme.typography.labelLarge,
            )
        }
    }
}

@Preview
@Composable
private fun KeyboardPreview() {
    Keyboard(
        keyButtons = defaultKeyButtons,
        checkWordKeyState = remember {
            mutableStateOf(CheckWordKeyState.Disabled)
        },
        deleteKeyState = remember {
            mutableStateOf(DeleteKeyState.Disabled)
        },
        onKeyButtonClick = {},
    )
}