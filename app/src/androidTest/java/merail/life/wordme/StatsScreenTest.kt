package merail.life.wordme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import merail.life.stats.navigateToStats
import merail.life.wordme.navigation.WordMeNavHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class StatsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun statsScreen_displaysAllLabels() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigateToStats()
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Данные об\u00A0игре")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Слов отгадано")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("% побед")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Текущая серия")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Лучшая серия")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("В\u00A0среднем попыток")
            .assertIsDisplayed()
    }
}
