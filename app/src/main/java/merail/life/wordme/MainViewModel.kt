package merail.life.wordme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import merail.life.core.extensions.suspendableRunCatching
import merail.life.core.log.IWordMeLogger
import merail.life.game.api.IGameLoader
import merail.life.wordme.state.MainState
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val gameLoader: IGameLoader,
    private val logger: IWordMeLogger,
): ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _mainState = MutableStateFlow<MainState>(MainState.Loading)
    val mainState: StateFlow<MainState> = _mainState

    init {
        viewModelScope.launch {
            suspendableRunCatching {
                gameLoader.loadInitialData()

                _mainState.value = MainState.Success
            }.onFailure {
                logger.w(TAG, "Initial loading. Failure", it)

                _mainState.value = MainState.LoadingError
            }
        }
    }
}
