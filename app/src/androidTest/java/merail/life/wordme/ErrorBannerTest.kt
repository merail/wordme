package merail.life.wordme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import merail.life.domain.WordModel
import merail.life.server.api.IServerRepository
import merail.life.server.impl.di.ServerModule
import merail.life.wordme.navigation.WordMeNavHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(ServerModule::class)
class ErrorBannerTest {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeServerModule {

        @Provides
        @Singleton
        fun provideFakeServerRepository(): IServerRepository = object : IServerRepository {
            override suspend fun getDayWord(id: Int): WordModel = WordModel("тесты")
            override suspend fun isWordExist(word: String): Boolean = throw IOException("test error")
            override suspend fun getGameCountdownStartDate(): String = "01.01.2024"
        }
    }

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
    fun errorBanner_isNotDisplayedByDefault() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Ошибка при проверке слова")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Новое слово не загрузилось. Перезапустите приложение")
            .assertDoesNotExist()
    }

    @Test
    fun errorBanner_closeButton_isNotDisplayedByDefault() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("close error banner icon")
            .assertDoesNotExist()
    }

    @Test
    fun errorBanner_wordExistenceCheckError_isDisplayed() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        typeWordAndClickOk()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithText("Ошибка при проверке слова")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Ошибка при проверке слова")
            .assertIsDisplayed()
    }

    @Test
    fun errorBanner_closeButton_isDisplayed_whenErrorShown() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        typeWordAndClickOk()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithContentDescription("close error banner icon")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithContentDescription("close error banner icon")
            .assertIsDisplayed()
    }

    @Test
    fun errorBanner_closeButton_dismissesError() {
        composeTestRule.setContent {
            navController = rememberNavController()
            WordMeNavHost(
                navController = navController,
                isNoInternet = false,
            )
        }

        composeTestRule.waitForIdle()

        typeWordAndClickOk()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithText("Ошибка при проверке слова")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithContentDescription("close error banner icon")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Ошибка при проверке слова")
            .assertDoesNotExist()
    }

    private fun typeWordAndClickOk() {
        composeTestRule.onNode(hasText("С") and hasClickAction()).performClick()
        composeTestRule.onNode(hasText("Л") and hasClickAction()).performClick()
        composeTestRule.onNode(hasText("О") and hasClickAction()).performClick()
        composeTestRule.onNode(hasText("В") and hasClickAction()).performClick()
        composeTestRule.onNode(hasText("О") and hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithContentDescription("check word icon")
            .performClick()
    }
}
