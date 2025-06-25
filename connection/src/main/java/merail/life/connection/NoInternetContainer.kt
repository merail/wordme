package merail.life.connection

import androidx.compose.runtime.Composable
import merail.life.connection.view.NoInternetScreen

@Composable
fun NoInternetContainer(
    onReconnect: () -> Unit,
) = NoInternetScreen(
    onReconnect = onReconnect,
)