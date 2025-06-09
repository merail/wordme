package merail.life.database.api

import merail.life.domain.WordIdModel
import merail.life.domain.WordModel

interface IDatabaseRepository {

    suspend fun getDayWordId(
        id: Int,
    ): WordIdModel

    suspend fun getWordOfTheDay(
        id: Int,
    ): WordModel

    suspend fun isWordExist(
        word: String,
    ): Boolean
}