package merail.life.database.impl.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import merail.life.database.api.IDatabaseRepository
import merail.life.database.impl.repository.guessedWordId.GuessedWordsIdsDatabaseProvider
import merail.life.database.impl.repository.guessedWordId.toModel
import merail.life.database.impl.repository.word.WordsDatabase
import merail.life.database.impl.repository.word.toModel
import javax.inject.Inject

internal class DatabaseRepository @Inject constructor(
    wordsDatabase: WordsDatabase,
    private val guessedWordsIdsDatabaseProvider: GuessedWordsIdsDatabaseProvider,
) : IDatabaseRepository {

    private val wordDao = wordsDatabase.wordDao()

    private val guessedWordsIdsDatabase by lazy {
        guessedWordsIdsDatabaseProvider.get().guessedWordsIdDao()
    }

    override fun initIdsDatabase(password: String) {
        guessedWordsIdsDatabaseProvider.init(password)
    }

    override suspend fun getDayWordId(
        id: Int,
    ) = withContext(Dispatchers.IO) {
        guessedWordsIdsDatabase.getDayWordId(id).toModel()
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