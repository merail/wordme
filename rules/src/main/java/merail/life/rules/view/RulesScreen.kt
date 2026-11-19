package merail.life.rules.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import merail.life.design.WordMeTheme
import merail.life.design.components.CrossIconButton
import merail.life.design.components.LetterCell
import merail.life.design.components.LetterCellState
import merail.life.rules.R

private const val CORRECT_EXAMPLE_WORD = "КУЛАК"
private const val PRESENT_EXAMPLE_WORD = "СКАЛА"
private const val ABSENT_EXAMPLE_WORD = "ТЕКСТ"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RulesScreen(
    onDismiss: () -> Unit,
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
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                ),
        ) {
            CrossIconButton(
                onClick = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        onDismiss()
                    }
                },
                modifier = Modifier.align(Alignment.End),
            )

            Text(
                text = stringResource(R.string.rules_title),
                style = WordMeTheme.typography.displaySmall,
                color = WordMeTheme.colors.textPositive,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        top = 12.dp,
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 20.dp,
                    )
                    .verticalScroll(
                        state = rememberScrollState(),
                    )
                    .padding(
                        bottom = 32.dp,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.rules_intro),
                    style = WordMeTheme.typography.bodyMedium,
                )

                RulesDivider()

                RuleExample(
                    word = CORRECT_EXAMPLE_WORD,
                    highlightIndex = 0,
                    state = LetterCellState.CORRECT,
                    description = stringResource(R.string.rules_correct_description),
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                )

                RuleExample(
                    word = PRESENT_EXAMPLE_WORD,
                    highlightIndex = 1,
                    state = LetterCellState.PRESENT,
                    description = stringResource(R.string.rules_present_description),
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                )

                RuleExample(
                    word = ABSENT_EXAMPLE_WORD,
                    highlightIndex = 2,
                    state = LetterCellState.ABSENT,
                    description = stringResource(R.string.rules_absent_description),
                )

                RulesDivider()

                Text(
                    text = stringResource(R.string.rules_outro),
                    style = WordMeTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun RulesDivider() {
    HorizontalDivider(
        color = WordMeTheme.colors.elementSecondary,
        modifier = Modifier
            .padding(
                vertical = 20.dp,
            ),
    )
}

@Composable
private fun RuleExample(
    word: String,
    highlightIndex: Int,
    state: LetterCellState,
    description: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = WordMeTheme.colors.elementTertiary,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            word.forEachIndexed { index, char ->
                LetterCell(
                    letter = char.toString(),
                    state = if (index == highlightIndex) state else LetterCellState.DEFAULT,
                    modifier = Modifier
                        .weight(
                            weight = 1f,
                        ),
                )
            }
        }
        Text(
            text = description,
            style = WordMeTheme.typography.bodyMedium,
        )
    }
}
