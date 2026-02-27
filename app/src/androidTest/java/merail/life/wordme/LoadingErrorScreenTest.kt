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
class LoadingErrorScreenTest {

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
    fun loadingErrorScreen_displaysTitle() {
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
    fun loadingErrorScreen_displaysSubtitle() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = true,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Попробуйте обновить экран")
            .assertIsDisplayed()
    }

    @Test
    fun loadingErrorScreen_displaysReconnectButton() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = true,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Обновить")
            .assertIsDisplayed()
    }

    @Test
    fun loadingErrorScreen_reconnectButtonIsClickable() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isLoadingError = true,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("Обновить") and hasClickAction())
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()
    }
}
