package merail.life.wordme

sealed class MainState {
    object Loading : MainState()

    object NoInternetConnection : MainState()

    object Success : MainState()
}