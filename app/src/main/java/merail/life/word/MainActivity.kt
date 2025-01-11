package merail.life.word

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import merail.life.word.design.WordMeTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()/*.setKeepOnScreenCondition {
            true
        }*/

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            WordMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    WordMeApp()
                }
            }
        }
    }
}