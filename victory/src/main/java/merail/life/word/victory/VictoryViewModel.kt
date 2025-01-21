package merail.life.word.victory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import merail.life.word.core.extensions.getTimeUntilNextDay
import javax.inject.Inject

@HiltViewModel
internal class VictoryViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "VictoryViewModel"
    }

    var timeUntilNextDay by mutableStateOf(getTimeUntilNextDay())
        private set

    init {
        viewModelScope.launch {
            while (true) {
                timeUntilNextDay = getTimeUntilNextDay()
                delay(1000L)
            }
        }
    }
}

