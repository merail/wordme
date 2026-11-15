package merail.life.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import merail.life.connection.state.ReloadingState
import merail.life.core.extensions.suspendableRunCatching
import merail.life.core.log.IWordMeLogger
import merail.life.game.api.IGameLoader
import javax.inject.Inject

@HiltViewModel
internal class LoadingErrorViewModel @Inject constructor(
    private val gameLoader: IGameLoader,
    private val logger: IWordMeLogger,
) : ViewModel() {

    companion object {
        private const val TAG = "LoadingErrorViewModel"
    }

    private val _reloadingState = MutableStateFlow<ReloadingState>(ReloadingState.None)
    val reloadingState: StateFlow<ReloadingState> = _reloadingState

    fun fetchInitialData() = viewModelScope.launch {
        suspendableRunCatching {
            _reloadingState.value = ReloadingState.Reloading

            gameLoader.loadInitialData()

            _reloadingState.value = ReloadingState.Success
        }.onFailure {
            logger.w(TAG, "Initial loading. Failure", it)

            _reloadingState.value = ReloadingState.None
        }
    }
}
