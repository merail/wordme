package merail.life.word.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import merail.life.word.design.WordMeTheme
import merail.life.word.game.GameViewModel
import merail.life.word.game.R

@Composable
internal fun ResultBoard(
    viewModel: GameViewModel,
    keyboardHeight: MutableState<Int>,
    onResultClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(
                start = 24.dp,
                end = 24.dp,
                bottom = LocalContext.current.bottomPadding,
            )
            .fillMaxWidth()
            .height(
                height = with(LocalDensity.current) {
                    keyboardHeight.value.toDp()
                },
            )
            .clickable(
                onClick = onResultClick,
            )
            .background(
                color = WordMeTheme.colors.elementDisabled,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(16.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(merail.life.word.design.R.drawable.ic_info),
            tint = Color.Unspecified,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = ripple(
                        bounded = false,
                    ),
                    onClick = onResultClick,
                ),
        )

        Column(
            modifier = Modifier
                .padding(
                    vertical = 16.dp,
                )
                .align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = stringResource(R.string.new_word_time_label),
                style = WordMeTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )

            Text(
                text = viewModel.timeUntilNextDay,
                style = WordMeTheme.typography.displaySmall,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        top = 4.dp,
                    ),
            )
        }
    }
}