package merail.life.word.database.api

import merail.life.word.database.api.model.WordModel

interface IDatabaseRepository {

    suspend fun getCurrentWord(
        id: Int,
    ): WordModel

    suspend fun isWordExist(
        word: String,
    ): Boolean
}