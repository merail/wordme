package merail.life.word.navigation.domain

import kotlinx.serialization.Serializable

sealed class NavigationRoute {

    @Serializable
    data object Game : NavigationRoute()

    @Serializable
    data class Result(
        val isVictory: Boolean,
    ) : NavigationRoute()
}