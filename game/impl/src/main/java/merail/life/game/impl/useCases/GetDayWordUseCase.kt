package merail.life.game.impl.useCases

import merail.life.core.extensions.suspendableRunCatching
import merail.life.core.log.IWordMeLogger
import merail.life.game.impl.GameViewModel.Companion.TAG
import merail.life.server.api.IServerRepository
import javax.inject.Inject

internal class GetDayWordUseCase @Inject constructor(
    private val serverRepository: IServerRepository,
    private val logger: IWordMeLogger,
) {
    suspend operator fun invoke(id: Int) = suspendableRunCatching {
        logger.d(TAG, "Getting day word with id=$id. Start")
        serverRepository.getDayWord(id)
    }.onSuccess {
        logger.d(TAG, "Getting day word with id=$id. Success")
    }.onFailure {
        logger.w(TAG, "Getting day word with id=$id. Failure", it)
    }
}
