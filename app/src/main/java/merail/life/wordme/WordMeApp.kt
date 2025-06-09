package merail.life.wordme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import merail.life.navigation.graph.WordMeNavHost

@Composable
internal fun WordMeApp(
    navController: NavHostController = rememberNavController(),
) {
    WordMeNavHost(
        navController = navController,
    )
}