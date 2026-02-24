package merail.life.game.impl.useCases

import merail.life.core.extensions.suspendableRunCatching
import merail.life.core.log.IWordMeLogger
import merail.life.game.impl.GameViewModel.Companion.TAG
import merail.life.server.api.IServerRepository
import javax.inject.Inject

internal class CheckWordExistenceUseCase @Inject constructor(
    private val serverRepository: IServerRepository,
    private val logger: IWordMeLogger,
) {
    suspend operator fun invoke(word: String) = suspendableRunCatching {
        logger.d(TAG, "Checking word \"$word\" existing. Start")
        serverRepository.isWordExist(word)
    }.onSuccess {
        logger.d(TAG, "Checking word \"$word\" existing. Success (existing=$it)")
    }.onFailure {
        logger.w(TAG, "Checking word \"$word\" existing. Failure", it)
    }
}
