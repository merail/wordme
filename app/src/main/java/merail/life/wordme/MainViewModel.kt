package merail.life.wordme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.config.api.IConfigRepository
import merail.life.core.extensions.suspendableRunCatching
import merail.life.core.log.IWordMeLogger
import merail.life.server.api.IServerRepository
import merail.life.game.api.IGameRepository
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import merail.life.wordme.state.MainState
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val configRepository: IConfigRepository,
    private val serverRepository: IServerRepository,
    private val storeRepository: IStoreRepository,
    private val timeRepository: ITimeRepository,
    private val gameRepository: IGameRepository,
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
                configRepository.authAnonymously()

                val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()

                val lastSinceStartDaysCount = storeRepository.getDaysSinceStartCount().first()

                gameRepository.setDayWord(
                    dayWord = serverRepository.getDayWord(
                        id = daysSinceStartCount + 1,
                    ),
                )

                if (lastSinceStartDaysCount == daysSinceStartCount) {
                    gameRepository.setKeyForms(
                        keyForms = storeRepository.loadKeyForms().first(),
                    )
                } else {
                    storeRepository.saveDaysSinceStartCount(daysSinceStartCount)
                    storeRepository.removeKeyForms()
                }

                val lastVictoryDay = storeRepository.getLastVictoryDay().first()
                if (daysSinceStartCount - lastVictoryDay > 1) {
                    storeRepository.resetVictoriesRowCount()
                }

                _mainState.value = MainState.Success
            }.onFailure {
                logger.w(TAG, "Initial loading. Failure", it)

                _mainState.value = MainState.LoadingError
            }
        }
    }
}