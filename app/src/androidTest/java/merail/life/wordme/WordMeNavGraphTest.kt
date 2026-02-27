package merail.life.wordme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import merail.life.game.impl.navigateToGame
import merail.life.result.navigateToResult
import merail.life.stats.navigateToStats
import merail.life.wordme.navigation.WordMeNavHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class WordMeNavGraphTest {

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
    fun startDestination_isGameScreen_whenInternetAvailable() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Й")
            .assertIsDisplayed()
    }

    @Test
    fun startDestination_isLoadingErrorScreen_whenLoadingError() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = true,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Что-то пошло не\u00A0так")
            .assertIsDisplayed()
    }

    @Test
    fun navigateToResultScreen_displaysResultDialog() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigateToResult(
                isVictory = true,
                attemptsCount = 3,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Победа!")
            .assertIsDisplayed()
    }

    @Test
    fun navigateToResultScreen_displaysDefeatDialog() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigateToResult(
                isVictory = false,
                attemptsCount = 6,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Поражение!")
            .assertIsDisplayed()
    }

    @Test
    fun navigateToStatsScreen_displaysStatsDialog() {
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
    }

    @Test
    fun navigateToGameScreen_fromLoadingErrorScreen() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = true,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigateToGame()
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Й")
            .assertIsDisplayed()
    }
}
