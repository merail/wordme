package merail.life.word.data.impl.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import merail.life.word.database.api.IDatabaseRepository
import javax.inject.Inject

internal class DatabaseRepository @Inject constructor(
    database: WordsDatabase,
) : IDatabaseRepository {

    private val dao = database.wordsElementDao()

    override suspend fun getWordOfTheDay(
        id: Int,
    ) = withContext(Dispatchers.IO) {
        dao.getWordOfTheDay(id).toModel()
    }

    override suspend fun isWordExist(
        word: String,
    ) = withContext(Dispatchers.IO) {
        dao.isWordExist(word) != null
    }
}