package merail.life.wordme.state

sealed class MainState {
    data object Loading : MainState()

    data object LoadingError : MainState()

    data object Success : MainState()
}