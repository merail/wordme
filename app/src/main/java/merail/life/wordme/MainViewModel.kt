package merail.life.wordme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.config.api.IConfigRepository
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.GameStore
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val configRepository: IConfigRepository,
    private val databaseRepository: IDatabaseRepository,
    private val storeRepository: IStoreRepository,
    private val timeRepository: ITimeRepository,
): ViewModel() {

    var mainState = MutableStateFlow<MainState>(MainState.Loading)
        private set

    init {
        viewModelScope.launch {
            runCatching {
                configRepository.fetchAndActivateRemoteConfig()

                databaseRepository.initIdsDatabase(
                    password = configRepository.getIdsDatabasePassword().first(),
                )

                val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()
                val dayWordId = databaseRepository.getDayWordId(daysSinceStartCount + 1)

                val lastSinceStartDaysCount = storeRepository.getDaysSinceStartCount().first()

                GameStore.dayWord.value = databaseRepository.getDayWord(dayWordId.value)

                if (lastSinceStartDaysCount == daysSinceStartCount) {
                    GameStore.keyForms = storeRepository.loadKeyForms().first()
                } else {
                    storeRepository.saveDaysSinceStartCount(daysSinceStartCount)
                    storeRepository.removeKeyForms()
                }

                val lastVictoryDay = storeRepository.getLastVictoryDay().first()
                if (daysSinceStartCount - lastVictoryDay > 1) {
                    storeRepository.resetVictoriesRowCount()
                }

                mainState.value = MainState.Success
            }.onFailure {
                if (it is NoInternetConnectionException) {
                    mainState.value = MainState.NoInternetConnection
                }
            }
        }
    }
}