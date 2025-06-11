package merail.life.wordme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.core.extensions.getDaysSinceStartCount
import merail.life.database.api.IDatabaseRepository
import merail.life.domain.GameStore
import merail.life.store.api.IStoreRepository
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val storeRepository: IStoreRepository,
): ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            val daysSinceStartCount = getDaysSinceStartCount()
            val dayWordId = databaseRepository.getDayWordId(daysSinceStartCount + 1)

            val lastSinceInitDaysCount = storeRepository.getDaysSinceStartCount().first()

            GameStore.dayWord = databaseRepository.getDayWord(dayWordId.value)

            if (lastSinceInitDaysCount == daysSinceStartCount) {
                GameStore.keyForms = storeRepository.loadKeyForms().first()
            } else {
                storeRepository.saveDaysSinceStartCount(daysSinceStartCount)
                storeRepository.removeKeyForms()
            }

            isLoading = false
        }
    }
}