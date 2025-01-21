package merail.life.word.result.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import merail.life.word.design.WordMeTheme
import merail.life.word.result.R
import merail.life.word.result.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ResultScreen(
    onDismiss: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel<ResultViewModel>(),
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = WordMeTheme.colors.screenBackgroundSecondary,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_cross),
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
                    ) {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            onDismiss()
                        }
                    },
            )

            Text(
                text = stringResource(
                    id = if (viewModel.isVictory) {
                        R.string.victory_title
                    } else {
                        R.string.defeat_title
                    },
                ),
                style = WordMeTheme.typography.displaySmall,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )

            Column(
                modifier = Modifier
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp,
                    )
                    .fillMaxWidth()
                    .background(
                        color = WordMeTheme.colors.elementSecondary,
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.victory_time_label),
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
}