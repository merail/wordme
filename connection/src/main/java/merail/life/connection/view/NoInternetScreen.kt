package merail.life.connection.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import merail.life.connection.NoInternetViewModel
import merail.life.connection.R
import merail.life.connection.state.ReloadingState
import merail.life.design.WordMeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoInternetScreen(
    onReconnect: () -> Unit,
    viewModel: NoInternetViewModel = hiltViewModel<NoInternetViewModel>(),
) {
    val state = viewModel.reloadingState.collectAsState().value

    if (state is ReloadingState.Success) {
        onReconnect()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center),
        ) {
            Image(
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.ic_no_internet,
                ),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )

            Text(
                text = stringResource(R.string.no_inernet_title),
                style = WordMeTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 64.dp,
                    ),
            )

            Text(
                text = stringResource(R.string.no_internet_subtitle),
                style = WordMeTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                    ),
            )
        }

        Button(
            onClick = {
                viewModel.fetchInitialData()
            },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = WordMeTheme.colors.elementPositive,
            ),
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    shape = RoundedCornerShape(12.dp),
                    color = WordMeTheme.colors.elementPositive,
                )
                .align(Alignment.BottomCenter),
        ) {
            if (state is ReloadingState.Reloading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterVertically),
                )
            } else {
                Text(
                    text = stringResource(R.string.no_internet_reconnect_button),
                    style = WordMeTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}