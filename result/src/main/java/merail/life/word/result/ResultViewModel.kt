package merail.life.word.result

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import merail.life.word.core.extensions.TimeCounter
import merail.life.word.navigation.domain.NavigationRoute
import javax.inject.Inject

@HiltViewModel
internal class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "ResultViewModel"
    }

    val isVictory = savedStateHandle.toRoute<NavigationRoute.Result>().isVictory

    var timeUntilNextDay by mutableStateOf(TimeCounter.getTimeUntilNextDay().first)
        private set

    var isNextDay by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            while (isNextDay.not()) {
                val (time, isNextDay) = TimeCounter.getTimeUntilNextDay()
                timeUntilNextDay = time
                if (isNextDay) {
                    this@ResultViewModel.isNextDay = true
                }
                delay(1000L)
            }
        }
    }
}

