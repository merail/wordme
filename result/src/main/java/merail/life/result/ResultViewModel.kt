package merail.life.result

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import merail.life.domain.constants.IS_TEST_ENVIRONMENT
import merail.life.time.api.ITimeRepository
import merail.life.wordme.navigation.domain.NavigationRoute
import javax.inject.Inject

@HiltViewModel
internal class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val timeRepository: ITimeRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "ResultViewModel"
    }

    val isVictory = savedStateHandle.toRoute<NavigationRoute.Result>().isVictory

    val attemptsCount = savedStateHandle.toRoute<NavigationRoute.Result>().attemptsCount

    var timeUntilNextDay by mutableStateOf("")
        private set

    var isNextDay by mutableStateOf(false)
        private set

    private val isTestEnvironment = savedStateHandle.get<Boolean>(IS_TEST_ENVIRONMENT) == true

    init {
        if (isTestEnvironment.not()) {
            viewModelScope.launch {
                timeRepository.getTimeUntilNextDay().collect { (time, isNextDay) ->
                    onSecondCount(time, isNextDay)
                }
            }
        }
    }

    @VisibleForTesting
    fun onSecondCount(
        time: String,
        isNextDay: Boolean,
    ) {
        timeUntilNextDay = time
        this@ResultViewModel.isNextDay = isNextDay
    }
}

