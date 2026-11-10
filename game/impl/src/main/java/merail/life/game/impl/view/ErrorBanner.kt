package merail.life.game.impl.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import merail.life.design.WordMeTheme
import merail.life.design.components.CrossIconButton
import merail.life.game.impl.R
import merail.life.game.impl.state.GameErrorState
import merail.life.game.impl.state.isGettingError
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
        val coroutineScope = rememberCoroutineScope()

        val isDismissible = errorState.isGettingError.not()

        val dismissState = rememberSwipeToDismissBoxState()

        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                onDismiss()
            }
        }

        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {},
            enableDismissFromStartToEnd = isDismissible,
            enableDismissFromEndToStart = isDismissible,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 80.dp,
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(WordMeTheme.colors.elementNegativeSecondary)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp,
                    ),
            ) {
                Text(
                    text = errorState.errorText(),
                    style = WordMeTheme.typography.bodyLarge,
                    color = WordMeTheme.colors.textInversePrimary,
                    modifier = Modifier
                        .weight(1f),
                )
                when {
                    errorState.isGettingError -> CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = WordMeTheme.colors.textInversePrimary,
                        modifier = Modifier
                            .size(24.dp),
                    )
                    errorState.isVisible -> CrossIconButton(
                        backgroundColor = WordMeTheme.colors.elementNegativeTertiary,
                        onClick = {
                            coroutineScope.launch {
                                onDismiss()
                            }
                        },
                    )
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
