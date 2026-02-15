package merail.life.server.impl.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import merail.life.server.api.IServerRepository
import merail.life.domain.WordModel
import javax.inject.Inject

internal class ServerRepository @Inject constructor(
    private val serverApi: ServerApi,
) : IServerRepository {

    override suspend fun getDayWord(
        id: Int,
    ) = withContext(Dispatchers.IO) {
        WordModel(serverApi.getDayWord(id))
    }

    override suspend fun isWordExist(
        word: String,
    ) = withContext(Dispatchers.IO) {
        serverApi.isWordExist(word)
    }

    override suspend fun getGameCountdownStartDate() = withContext(Dispatchers.IO) {
        serverApi.getGameCountdownStartDate()
    }
}
