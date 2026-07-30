package merail.life.game.impl.view

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import merail.life.game.impl.R
import merail.life.game.impl.model.Key
import merail.life.game.impl.model.KeyCell
import merail.life.game.impl.model.KeyState
import merail.life.game.impl.state.WordCheckState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RulesBottomSheet(
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
                .padding(16.dp),
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

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
            )

            RuleExample(
                word = "КУЛАК",
                highlightIndex = 0,
                state = KeyState.CORRECT,
                description = stringResource(R.string.rules_correct_description),
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
            )

            RuleExample(
                word = "СКАЛА",
                highlightIndex = 1,
                state = KeyState.PRESENT,
                description = stringResource(R.string.rules_present_description),
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
            )

            RuleExample(
                word = "ТЕКСТ",
                highlightIndex = 2,
                state = KeyState.ABSENT,
                description = stringResource(R.string.rules_absent_description),
            )
        }
    }
}

@Composable
private fun RuleExample(
    word: String,
    highlightIndex: Int,
    state: KeyState,
    description: String,
) {
    val scope = rememberCoroutineScope()

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
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            word.forEachIndexed { index, char ->
                KeyForm(
                    scope = scope,
                    row = 0,
                    column = index,
                    keyForm = KeyCell(
                        key = Key.getKeyFromValue(
                            value = char.toString(),
                        ),
                        state = if (index == highlightIndex) state else KeyState.DEFAULT,
                    ),
                    wordCheckState = WordCheckState.None,
                    isLastFilledRow = true,
                    isNextDay = false,
                    onFlipAnimationEnd = {},
                )
            }
        }
        Text(
            text = description,
            style = WordMeTheme.typography.bodyMedium,
        )
    }
}
