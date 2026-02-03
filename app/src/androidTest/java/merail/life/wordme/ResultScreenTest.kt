package merail.life.wordme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import merail.life.result.navigateToResult
import merail.life.wordme.navigation.WordMeNavHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ResultScreenTest {

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
    fun resultScreen_displaysVictoryContent() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
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

        composeTestRule
            .onNodeWithText("Отличная игра! Завтра появится новое слово\u00A0— не\u00A0упусти шанс разгадать его")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("До\u00A0появления нового слова:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Число попыток:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("3/6")
            .assertIsDisplayed()
    }

    @Test
    fun resultScreen_displaysDefeatContent() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
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

        composeTestRule
            .onNodeWithText("Сегодня слово не\u00A0удалось разгадать, но\u00A0не\u00A0переживайте. Завтра появится шанс попробовать снова!")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("До\u00A0появления нового слова:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Число попыток:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("6/6")
            .assertIsDisplayed()
    }
}
