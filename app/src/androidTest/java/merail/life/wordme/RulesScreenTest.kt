package merail.life.wordme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import merail.life.rules.navigateToRules
import merail.life.wordme.navigation.WordMeNavHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class RulesScreenTest {

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
    fun rulesScreen_displaysAllContent() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.runOnUiThread {
            navController.navigateToRules()
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Как играть")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                text = "Каждый день загадывается новое слово",
                substring = true,
            )
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Буква есть в\u00A0слове и\u00A0стоит на\u00A0своём месте")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Буква есть в\u00A0слове, но\u00A0стоит не\u00A0на\u00A0этом месте")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Этой буквы нет в\u00A0слове")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                text = "Буквы в\u00A0слове могут повторяться",
                substring = true,
            )
            .performScrollTo()
            .assertIsDisplayed()
    }
}
