package merail.life.result

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
import merail.life.core.extensions.getTimeUntilNextDay
import merail.life.wordme.navigation.domain.NavigationRoute
import javax.inject.Inject

@HiltViewModel
internal class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "ResultViewModel"
    }

    val isVictory = savedStateHandle.toRoute<NavigationRoute.Result>().isVictory

    val attemptsCount = savedStateHandle.toRoute<NavigationRoute.Result>().attemptsCount

    var timeUntilNextDay by mutableStateOf(getTimeUntilNextDay().first)
        private set

    var isNextDay by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            while (isNextDay.not()) {
                val (time, isNextDay) = getTimeUntilNextDay()
                timeUntilNextDay = time
                if (isNextDay) {
                    this@ResultViewModel.isNextDay = true
                }
                delay(1000L)
            }
        }
    }
}

