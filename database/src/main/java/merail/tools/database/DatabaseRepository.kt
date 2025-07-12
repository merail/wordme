package merail.tools.database

import android.util.Log
import merail.tools.database.randomedWordsIds.RandomedWordsIdsDatabase
import merail.tools.database.randomedWordsIds.RandomedWordsIdsEncryptedDatabase
import merail.tools.database.words.WordsDatabase
import merail.tools.database.words.WordsEncryptedDatabase
import merail.tools.database.wordsIds.WordsIdsDatabase
import merail.tools.database.wordsIds.WordsIdsEncryptedDatabase
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    wordsDatabase: WordsDatabase,
    private val wordsEncryptedDatabase: WordsEncryptedDatabase,
    wordsIdsDatabase: WordsIdsDatabase,
    private val wordsIdsEncryptedDatabase: WordsIdsEncryptedDatabase,
    randomedWordsIdsDatabase: RandomedWordsIdsDatabase,
    private val randomedWordsIdsEncryptedDatabase: RandomedWordsIdsEncryptedDatabase,
) : IDatabaseRepository {

    private val encryptedWordDao = wordsEncryptedDatabase.dao()

    private val wordDao = wordsDatabase.dao()

    private val wordsIdsDao = wordsIdsDatabase.dao()

    private val encryptedWordsIdsDao = wordsIdsEncryptedDatabase.dao()

    private val randomedWordsIdsDao = randomedWordsIdsDatabase.dao()

    private val encryptedRandomedWordsIdsDao = randomedWordsIdsEncryptedDatabase.dao()

    override suspend fun encryptWordsDB() {
        encryptedWordDao.insertAll(wordDao.getAll())
        wordsEncryptedDatabase.close()
    }

    override suspend fun encryptWordsIdsDB() {
        encryptedWordsIdsDao.insertAll(wordsIdsDao.getAll())
        wordsIdsEncryptedDatabase.close()
    }

    override suspend fun encryptRandomedWordsIdsDB() {
        encryptedRandomedWordsIdsDao.insertAll(randomedWordsIdsDao.getAll())
        randomedWordsIdsEncryptedDatabase.close()
    }

    override suspend fun checkEncryptedRandomedWordsIdsDB() {
        val a = encryptedRandomedWordsIdsDao.getAll()
        Log.d("AAAAAAAAAAAAAA", a.toString())
    }
}