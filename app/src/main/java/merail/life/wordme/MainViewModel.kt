package merail.life.wordme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.config.api.IConfigRepository
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.GameStore
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

    var isLoading by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            configRepository.fetchAndActivateRemoteConfig()

            val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()
            val dayWordId = databaseRepository.getDayWordId(daysSinceStartCount + 1)

            val lastSinceStartDaysCount = storeRepository.getDaysSinceStartCount().first()

            GameStore.dayWord = databaseRepository.getDayWord(dayWordId.value)

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

            isLoading = false
        }
    }
}