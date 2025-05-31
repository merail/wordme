package merail.life.word

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import merail.life.word.database.api.IDatabaseRepository
import merail.life.word.domain.GameStore
import merail.life.word.store.api.IStoreRepository
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
            val dayWordId = databaseRepository.getDayWordId(1)

            GameStore.dayWord = databaseRepository.getWordOfTheDay(dayWordId.value)

            GameStore.keyForms = storeRepository.loadKeyForms().first()

            isLoading = false
        }
    }
}