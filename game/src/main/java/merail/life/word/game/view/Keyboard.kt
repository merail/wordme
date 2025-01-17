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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import merail.life.word.design.WordMeTheme
import merail.life.word.game.R
import merail.life.word.game.state.Key
import merail.life.word.game.state.isControlKey


@Composable
internal fun Keyboard(
    onKeyClick: (Key) -> Unit,
) {
    val keyboardLayout = listOf(
        listOf(Key.А, Key.Б, Key.В, Key.Г, Key.Д, Key.Е, Key.Ж, Key.З, Key.И, Key.Й, Key.К, Key.Л),
        listOf(Key.М, Key.Н, Key.О, Key.П, Key.Р, Key.С, Key.Т, Key.У, Key.Ф, Key.Х, Key.Ц),
        listOf(Key.DEL, Key.Ч, Key.Ш, Key.Щ, Key.Ъ, Key.Ы, Key.Ь, Key.Э, Key.Ю, Key.Я, Key.OK),
    )

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
                    KeyButton(
                        key = key,
                        onKeyClick = onKeyClick,
                    )
                }
            }
        }
    }
}

@Composable
internal fun RowScope.KeyButton(
    key: Key,
    onKeyClick: (Key) -> Unit,
) {
    val contentWidth = (LocalConfiguration.current.screenWidthDp.dp -
            (keyboardRowHorizontalPadding * 2 +
                    (keyboardContentHorizontalPadding * 2 + keyboardContentBorder) * 12)) / 12
    val controlKeyAdditionalWidth = 8.dp

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
                WordMeTheme.colors.elementBackgroundPrimary
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

@Preview
@Composable
private fun KeyboardPreview() {
    Keyboard(
        onKeyClick = {},
    )
}