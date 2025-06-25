package merail.life.connection.state

sealed class ReloadingState {
    object None: ReloadingState()

    object Reloading : ReloadingState()

    object  Success : ReloadingState()
}