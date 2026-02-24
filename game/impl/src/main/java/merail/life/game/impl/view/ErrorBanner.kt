package merail.life.game.impl.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import merail.life.design.WordMeTheme
import merail.life.game.impl.R
import merail.life.game.impl.state.GameErrorState
import merail.life.game.impl.state.isVisible

@Composable
internal fun ErrorBanner(
    errorState: GameErrorState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = errorState.isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = topPadding,
            ),
    ) {
        key(errorState) {
            val dismissState = rememberSwipeToDismissBoxState()

            LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                    onDismiss()
                }
            }

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {},
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WordMeTheme.colors.elementNegative)
                        .padding(
                            start = 16.dp,
                            top = 12.dp,
                            bottom = 12.dp,
                        ),
                ) {
                    Text(
                        text = errorState.errorText(),
                        color = WordMeTheme.colors.textInversePrimary,
                        modifier = Modifier
                            .weight(1f),
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(40.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "close error banner icon",
                            tint = WordMeTheme.colors.textInversePrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameErrorState.errorText() = when (this) {
    is GameErrorState.WordExistingCheckError -> stringResource(R.string.error_word_existing_check_message)
    is GameErrorState.DayWordGettingError -> stringResource(R.string.error_day_word_getting_message)
    is GameErrorState.Default -> message ?: stringResource(R.string.error_default_message)
    is GameErrorState.Hidden -> ""
}
