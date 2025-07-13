package merail.life.database.impl.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import merail.life.database.api.IDatabaseRepository
import merail.life.database.impl.repository.id.IdsDatabaseProvider
import merail.life.database.impl.repository.id.toModel
import merail.life.database.impl.repository.word.WordsDatabase
import merail.life.database.impl.repository.word.toModel
import javax.inject.Inject

internal class DatabaseRepository @Inject constructor(
    wordsDatabase: WordsDatabase,
    private val idsDatabaseProvider: IdsDatabaseProvider,
) : IDatabaseRepository {

    private val wordDao = wordsDatabase.wordDao()

    private val idsDatabase by lazy {
        idsDatabaseProvider.get().idDao()
    }

    override fun initIdsDatabase(password: String) {
        idsDatabaseProvider.init(password)
    }

    override suspend fun getDayWordId(
        id: Int,
    ) = withContext(Dispatchers.IO) {
        val totalCount = idsDatabase.getCount()
        val totalBias = if (id % totalCount == 0) {
            1
        } else {
            0
        }
        idsDatabase.getDayId((id  + totalBias) % totalCount).toModel()
    }

    override suspend fun getDayWord(
        id: Int,
    ) = withContext(Dispatchers.IO) {
        wordDao.getDayWord(id).toModel()
    }

    override suspend fun isWordExist(
        word: String,
    ) = withContext(Dispatchers.IO) {
        wordDao.isWordExist(word) != null
    }
}