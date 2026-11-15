package merail.life.game.impl.loader

import kotlinx.coroutines.flow.first
import merail.life.config.api.IConfigRepository
import merail.life.domain.WordModel
import merail.life.game.api.IGameLoader
import merail.life.game.api.IGameRepository
import merail.life.game.impl.useCases.GetDayWordUseCase
import merail.life.store.api.IStoreRepository
import merail.life.time.api.ITimeRepository
import javax.inject.Inject

internal class GameLoader @Inject constructor(
    private val configRepository: IConfigRepository,
    private val getDayWordUseCase: GetDayWordUseCase,
    private val storeRepository: IStoreRepository,
    private val timeRepository: ITimeRepository,
    private val gameRepository: IGameRepository,
) : IGameLoader {

    override suspend fun loadInitialData() {
        configRepository.authAnonymously()

        val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()
        val lastDaysSinceStartCount = storeRepository.getDaysSinceStartCount().first()

        if (daysSinceStartCount == lastDaysSinceStartCount) {
            gameRepository.setDayWord(
                dayWord = getDayWord(
                    daysSinceStartCount = daysSinceStartCount,
                ),
            )
            gameRepository.setKeyForms(
                keyForms = storeRepository.loadKeyForms().first(),
            )
            resetVictoriesRowCountIfNeeded(
                daysSinceStartCount = daysSinceStartCount,
            )
        } else {
            startDay(
                daysSinceStartCount = daysSinceStartCount,
            )
        }
    }

    override suspend fun loadNewDay(): WordModel? {
        val daysSinceStartCount = timeRepository.getDaysSinceStartCount().first()
        val lastDaysSinceStartCount = storeRepository.getDaysSinceStartCount().first()

        return if (daysSinceStartCount > lastDaysSinceStartCount) {
            startDay(
                daysSinceStartCount = daysSinceStartCount,
            )
        } else {
            null
        }
    }

    private suspend fun startDay(
        daysSinceStartCount: Int,
    ): WordModel {
        val dayWord = getDayWord(
            daysSinceStartCount = daysSinceStartCount,
        )

        storeRepository.removeKeyForms()
        storeRepository.saveDaysSinceStartCount(
            count = daysSinceStartCount,
        )
        resetVictoriesRowCountIfNeeded(
            daysSinceStartCount = daysSinceStartCount,
        )

        gameRepository.setDayWord(
            dayWord = dayWord,
        )
        gameRepository.setKeyForms(
            keyForms = emptyList(),
        )

        return dayWord
    }

    private suspend fun getDayWord(
        daysSinceStartCount: Int,
    ) = getDayWordUseCase(
        id = daysSinceStartCount + 1,
    ).getOrThrow()

    private suspend fun resetVictoriesRowCountIfNeeded(
        daysSinceStartCount: Int,
    ) {
        val lastVictoryDay = storeRepository.getLastVictoryDay().first()
        if (daysSinceStartCount - lastVictoryDay > 1) {
            storeRepository.resetVictoriesRowCount()
        }
    }
}
