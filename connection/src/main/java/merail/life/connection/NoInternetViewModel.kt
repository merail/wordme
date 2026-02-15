package merail.life.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.config.api.IConfigRepository
import merail.life.connection.state.ReloadingState
import merail.life.core.extensions.suspendableRunCatching
import merail.life.core.log.IWordMeLogger
import merail.life.server.api.IServerRepository
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.game.api.IGameRepository
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import javax.inject.Inject

@HiltViewModel
internal class NoInternetViewModel @Inject constructor(
    private val configRepository: IConfigRepository,
    private val serverRepository: IServerRepository,
    private val storeRepository: IStoreRepository,
    private val timeRepository: ITimeRepository,
    private val gameRepository: IGameRepository,
    private val logger: IWordMeLogger,
) : ViewModel() {

    companion object {
        private const val TAG = "NoInternetViewModel"
    }

    private val _reloadingState = MutableStateFlow<ReloadingState>(ReloadingState.None)
    val reloadingState: StateFlow<ReloadingState> = _reloadingState

    fun fetchInitialData() = viewModelScope.launch {
        suspendableRunCatching {
            _reloadingState.value = ReloadingState.Reloading

            configRepository.authAnonymously()

            val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()

            val lastSinceStartDaysCount = storeRepository.getDaysSinceStartCount().first()

            gameRepository.setDayWord(
                dayWord = serverRepository.getDayWord(
                    id = daysSinceStartCount + 1,
                ),
            )

            if (lastSinceStartDaysCount == daysSinceStartCount) {
                gameRepository.setKeyForms(storeRepository.loadKeyForms().first())
            } else {
                storeRepository.saveDaysSinceStartCount(daysSinceStartCount)
                storeRepository.removeKeyForms()
            }

            val lastVictoryDay = storeRepository.getLastVictoryDay().first()
            if (daysSinceStartCount - lastVictoryDay > 1) {
                storeRepository.resetVictoriesRowCount()
            }

            _reloadingState.value = ReloadingState.Success
        }.onFailure {
            logger.w(TAG, it.message.orEmpty(), it)

            if (it is NoInternetConnectionException) {
                _reloadingState.value = ReloadingState.None
            } else {
                FirebaseCrashlytics.getInstance().recordException(it)
            }
        }
    }
}

