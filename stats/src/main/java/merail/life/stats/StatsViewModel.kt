package merail.life.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import merail.life.core.extensions.partOf
import merail.life.core.extensions.percentOf
import merail.life.store.api.IStoreRepository
import javax.inject.Inject

@HiltViewModel
internal class StatsViewModel @Inject constructor(
    storeRepository: IStoreRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "StatsViewModel"
    }

    private val _victoriesPercent = MutableStateFlow<String?>(null)
    val victoriesPercent: StateFlow<String?> = _victoriesPercent

    private val _attemptsRatio = MutableStateFlow<String?>(null)
    val attemptsRatio: StateFlow<String?> = _attemptsRatio

    private val _victoriesCount = MutableStateFlow<String?>(null)
    val victoriesCount: StateFlow<String?> = _victoriesCount

    private val _victoriesRowCount = MutableStateFlow<String?>(null)
    val victoriesRowCount: StateFlow<String?> = _victoriesRowCount

    private val _victoriesRowMaxCount = MutableStateFlow<String?>(null)
    val victoriesRowMaxCount: StateFlow<String?> = _victoriesRowMaxCount

    init {
        viewModelScope.launch {
            storeRepository.getStats().collect {
                _victoriesPercent.value = it.victoriesCount percentOf it.gamesCount
                _attemptsRatio.value = it.attemptsCount partOf it.victoriesCount
                _victoriesCount.value = it.victoriesCount.toString()
                _victoriesRowCount.value = it.victoriesRowCount.toString()
                _victoriesRowMaxCount.value = it.victoriesRowMaxCount.toString()
            }
        }
    }
}
