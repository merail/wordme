package merail.life.wordme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import merail.life.design.WordMeTheme
import merail.life.navigation.graph.WordMeNavHost

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().setKeepOnScreenCondition {
            viewModel.mainState.value is MainState.Loading
        }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            WordMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val state = viewModel.mainState.collectAsState().value

                    WordMeNavHost(
                        navController = rememberNavController(),
                        isNoInternet = state is MainState.NoInternetConnection,
                    )
                }
            }
        }
    }
}