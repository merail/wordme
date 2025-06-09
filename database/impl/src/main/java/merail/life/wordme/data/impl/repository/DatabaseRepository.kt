package merail.life.wordme.data.impl.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import merail.life.wordme.data.impl.repository.guessedWordId.GuessedWordsIdsDatabase
import merail.life.wordme.data.impl.repository.guessedWordId.toModel
import merail.life.wordme.data.impl.repository.word.WordsDatabase
import merail.life.wordme.data.impl.repository.word.toModel
import merail.life.database.api.IDatabaseRepository
import javax.inject.Inject

internal class DatabaseRepository @Inject constructor(
    wordsDatabase: WordsDatabase,
    guessedWordsIdsDatabase: GuessedWordsIdsDatabase,
) : IDatabaseRepository {

    private val wordDao = wordsDatabase.wordDao()

    private val guessedWordsIdsDatabase = guessedWordsIdsDatabase.guessedWordsIdDao()

    override suspend fun getDayWordId(
        id: Int,
    ) = withContext(Dispatchers.IO) {
        guessedWordsIdsDatabase.getDayWordId(id).toModel()
    }

    override suspend fun getWordOfTheDay(
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