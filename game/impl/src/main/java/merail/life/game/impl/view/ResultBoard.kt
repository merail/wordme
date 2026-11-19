package merail.life.game.impl.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import merail.life.design.R as designR
import merail.life.design.WordMeTheme
import merail.life.game.impl.R

@Composable
internal fun ResultBoard(
    timeUntilNextDay: String,
    onResultClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(
                horizontal = 24.dp,
            )
            .fillMaxWidth()
            .background(
                color = WordMeTheme.colors.elementTertiary,
                shape = RoundedCornerShape(24.dp),
            )
            .clickable(
                onClick = onResultClick,
            )
            .padding(16.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(designR.drawable.ic_info),
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
                .align(Alignment.CenterHorizontally)
                .padding(
                    vertical = 16.dp,
                ),
        ) {
            Text(
                text = stringResource(R.string.new_word_time_label),
                style = WordMeTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )

            Text(
                text = timeUntilNextDay,
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