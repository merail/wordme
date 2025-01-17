package merail.life.word.database.api

import merail.life.word.domain.WordModel

interface IDatabaseRepository {

    suspend fun getWordOfTheDay(
        id: Int,
    ): WordModel

    suspend fun isWordExist(
        word: String,
    ): Boolean
}