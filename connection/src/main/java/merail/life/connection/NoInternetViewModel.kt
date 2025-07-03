package merail.life.connection

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.config.api.IConfigRepository
import merail.life.connection.state.ReloadingState
import merail.life.core.extensions.suspendableRunCatching
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.constants.IS_TEST_ENVIRONMENT
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.game.api.IGameRepository
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import javax.inject.Inject

@HiltViewModel
internal class NoInternetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val configRepository: IConfigRepository,
    private val databaseRepository: IDatabaseRepository,
    private val storeRepository: IStoreRepository,
    private val timeRepository: ITimeRepository,
    private val gameRepository: IGameRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "NoInternetViewModel"
    }

    var reloadingState = MutableStateFlow<ReloadingState>(ReloadingState.None)
        private set

    private val isTestEnvironment = savedStateHandle.get<Boolean>(IS_TEST_ENVIRONMENT) == true

    fun fetchInitialData() = viewModelScope.launch {
        suspendableRunCatching {
            reloadingState.value = ReloadingState.Reloading

            configRepository.authAnonymously()

            configRepository.fetchInitialValues()

            databaseRepository.initIdsDatabase(
                password = configRepository.getIdsDatabasePassword().first(),
            )

            val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()
            val dayWordId = databaseRepository.getDayWordId(daysSinceStartCount + 1)

            val lastSinceStartDaysCount = storeRepository.getDaysSinceStartCount().first()

            gameRepository.setDayWord(databaseRepository.getDayWord(dayWordId.value))

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

            reloadingState.value = ReloadingState.Success
        }.onFailure {
            if (isTestEnvironment.not()) {
                Log.w(TAG, it)
            }

            if (it is NoInternetConnectionException) {
                reloadingState.value = ReloadingState.None
            } else {
                FirebaseCrashlytics.getInstance().recordException(it)
            }
        }
    }
}

