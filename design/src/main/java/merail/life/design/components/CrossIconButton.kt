package merail.life.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import merail.life.design.R
import merail.life.design.WordMeTheme

@Composable
fun CrossIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = WordMeTheme.colors.elementTertiary,
) {
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_cross),
        tint = Color.Unspecified,
        contentDescription = "cross icon button",
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
                onClick = onClick,
            ),
    )
}
