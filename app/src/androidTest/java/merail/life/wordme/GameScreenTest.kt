package merail.life.wordme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import merail.life.wordme.navigation.WordMeNavHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class GameScreenTest {

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
    fun gameScreen_displaysKeyboard() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Й")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Ц")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("У")
            .assertIsDisplayed()
    }

    @Test
    fun gameScreen_displaysSecondRowOfKeyboard() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Ф")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Ы")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("В")
            .assertIsDisplayed()
    }

    @Test
    fun gameScreen_displaysThirdRowOfKeyboard() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Я")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Ч")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("С")
            .assertIsDisplayed()
    }

    @Test
    fun gameScreen_keyButtonIsClickable() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("А") and hasClickAction())
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()
    }

    @Test
    fun gameScreen_canEnterMultipleLetters() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("С") and hasClickAction())
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("Л") and hasClickAction())
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("О") and hasClickAction())
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("В") and hasClickAction())
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("О") and hasClickAction())
            .performClick()

        composeTestRule.waitForIdle()
    }
}
