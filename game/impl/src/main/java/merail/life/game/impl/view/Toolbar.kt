package merail.life.game.impl.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import merail.life.design.R as designR
import merail.life.game.impl.R

@Composable
internal fun Toolbar(
    onRulesClick: () -> Unit,
    onInfoClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(
                top = topPadding,
                start = 24.dp,
                end = 24.dp,
            )
            .fillMaxWidth()
            .defaultMinSize(toolbarMinHeight),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(designR.drawable.ic_rules),
            contentDescription = stringResource(R.string.rules_button_content_description),
            modifier = Modifier
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = ripple(
                        bounded = false,
                    ),
                ) {
                    onRulesClick()
                },
        )
        Icon(
            imageVector = ImageVector.vectorResource(designR.drawable.ic_info),
            contentDescription = stringResource(R.string.stats_button_content_description),
            modifier = Modifier
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = ripple(
                        bounded = false,
                    ),
                ) {
                    onInfoClick()
                },
        )
    }
}