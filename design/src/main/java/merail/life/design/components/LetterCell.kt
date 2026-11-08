package merail.life.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import merail.life.design.WordMeTheme

enum class LetterCellState {
    DEFAULT,
    ABSENT,
    PRESENT,
    CORRECT,
    ;
}

private val letterCellShape = RoundedCornerShape(4.dp)

@Composable
fun LetterCell(
    letter: String,
    state: LetterCellState,
    modifier: Modifier = Modifier,
    rotationY: Float = 0f,
    defaultTextColor: Color = WordMeTheme.colors.textInversePrimary,
    textStyle: TextStyle = WordMeTheme.gameTypography.displaySmall,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 8 * density
            }
            .border(
                width = 1.dp,
                color = if (state == LetterCellState.DEFAULT) {
                    WordMeTheme.colors.elementPositive
                } else {
                    Color.Unspecified
                },
                shape = letterCellShape,
            )
            .aspectRatio(1f)
            .background(
                color = when (state) {
                    LetterCellState.DEFAULT -> WordMeTheme.colors.elementPrimary
                    LetterCellState.CORRECT -> WordMeTheme.colors.elementPositive
                    LetterCellState.PRESENT -> WordMeTheme.colors.elementInversePrimary
                    LetterCellState.ABSENT -> WordMeTheme.colors.elementSecondary
                },
                shape = letterCellShape,
            ),
    ) {
        Text(
            text = letter,
            color = when (state) {
                LetterCellState.DEFAULT -> defaultTextColor
                LetterCellState.CORRECT,
                LetterCellState.PRESENT,
                -> WordMeTheme.colors.textPrimary
                LetterCellState.ABSENT -> WordMeTheme.colors.textInversePrimary
            },
            textAlign = TextAlign.Center,
            style = textStyle,
            modifier = Modifier
                .graphicsLayer {
                    this.rotationY = rotationY
                    cameraDistance = 8 * density
                },
        )
    }
}

@Preview
@Composable
private fun LetterCellPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LetterCellState.entries.forEach { state ->
            LetterCell(
                letter = "А",
                state = state,
                modifier = Modifier.size(48.dp),
            )
        }
    }
}
