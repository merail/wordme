package merail.life.database.api

import merail.life.domain.WordIdModel
import merail.life.domain.WordModel

interface IDatabaseRepository {

    fun initIdsDatabase(
        password: String,
    )

    suspend fun getDayWordId(
        id: Int,
    ): WordIdModel

    suspend fun getDayWord(
        id: Int,
    ): WordModel

    suspend fun isWordExist(
        word: String,
    ): Boolean
}