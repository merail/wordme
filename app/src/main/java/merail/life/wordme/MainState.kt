package merail.life.wordme

sealed class MainState {
    data object Loading : MainState()

    data object NoInternetConnection : MainState()

    data object Success : MainState()
}