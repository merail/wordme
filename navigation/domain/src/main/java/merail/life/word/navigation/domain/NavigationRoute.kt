package merail.life.word.navigation.domain

import kotlinx.serialization.Serializable

sealed class NavigationRoute {

    @Serializable
    data object Stub : NavigationRoute()
}