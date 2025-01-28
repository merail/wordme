package merail.life.word.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import merail.life.word.core.extensions.partOf
import merail.life.word.core.extensions.percentOf
import merail.life.word.store.api.IStoreRepository
import javax.inject.Inject

@HiltViewModel
internal class StatsViewModel @Inject constructor(
    storeRepository: IStoreRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "StatsViewModel"
    }

    var victoriesPercent by mutableStateOf<String?>(null)

    var attemptsRatio by mutableStateOf<String?>(null)

    var victoriesCount by mutableStateOf<String?>(null)

    var victoriesRowCount by mutableStateOf<String?>(null)

    var victoriesRowMaxCount by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            storeRepository.getStats().collect {
                victoriesPercent = it.victoriesCount percentOf it.gamesCount
                attemptsRatio = it.attemptsCount partOf it.victoriesCount
                victoriesCount = it.victoriesCount.toString()
                victoriesRowCount = it.victoriesRowCount.toString()
                victoriesRowMaxCount = it.victoriesRowMaxCount.toString()
            }
        }
    }
}

