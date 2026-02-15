package merail.life.server.api

import merail.life.domain.WordModel

interface IServerRepository {

    suspend fun getDayWord(
        id: Int,
    ): WordModel

    suspend fun isWordExist(
        word: String,
    ): Boolean
}