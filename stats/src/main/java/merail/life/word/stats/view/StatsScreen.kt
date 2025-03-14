package merail.life.word.stats.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import merail.life.word.design.WordMeTheme
import merail.life.word.stats.R
import merail.life.word.stats.StatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatsScreen(
    onDismiss: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel<StatsViewModel>(),
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
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = null,
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(merail.life.word.design.R.drawable.ic_cross),
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
                text = stringResource(R.string.stats_tile),
                style = WordMeTheme.typography.displaySmall,
                color = WordMeTheme.colors.textPositive,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        top = 20.dp,
                    ),
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
            )

            Row {
                InfoBlock(
                    label = stringResource(R.string.stats_victories_count_label),
                    value = viewModel.victoriesCount.orEmpty(),
                    padding = PaddingValues(
                        end = 8.dp,
                    ),
                )

                InfoBlock(
                    label = stringResource(R.string.stats_successful_games_percent_label),
                    value = viewModel.victoriesPercent.orEmpty(),
                    padding = PaddingValues(
                        start = 8.dp,
                    ),
                )
            }

            Row(
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                    )
            ) {
                InfoBlock(
                    label = stringResource(R.string.stats_victories_row_count_label),
                    value = viewModel.victoriesRowCount.orEmpty(),
                    padding = PaddingValues(
                        end = 8.dp,
                    ),
                )

                InfoBlock(
                    label = stringResource(R.string.stats_victories_row_max_count_label),
                    value = viewModel.victoriesRowMaxCount.orEmpty(),
                    padding = PaddingValues(
                        start = 8.dp,
                    ),
                )
            }

            Row(
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                    )
                    .fillMaxWidth(),
            ) {
                InfoBlock(
                    label = stringResource(R.string.stats_average_attempts_count_label),
                    value = viewModel.attemptsRatio.orEmpty(),
                    padding = PaddingValues(0.dp),
                )
            }
        }
    }
}

@Composable
private fun RowScope.InfoBlock(
    label: String,
    value: String,
    padding: PaddingValues,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(padding)
            .background(
                color = WordMeTheme.colors.elementSecondary,
                shape = RoundedCornerShape(12.dp),
            )
            .weight(1f),
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 24.dp,
                ),
        ) {
            Text(
                text = label,
                style = WordMeTheme.typography.bodyMedium,
            )

            Text(
                text = value,
                style = WordMeTheme.typography.displaySmall,
                color = WordMeTheme.colors.textPositive,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(
                        top = 4.dp,
                    ),
            )
        }

        Icon(
            imageVector = ImageVector.vectorResource(merail.life.word.design.R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .size(20.dp)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = ripple(
                        bounded = false,
                    ),
                    onClick = {},
                ),
        )
    }
}