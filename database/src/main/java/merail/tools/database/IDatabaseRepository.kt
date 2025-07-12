package merail.tools.database

interface IDatabaseRepository {
    suspend fun encryptWordsDB()

    suspend fun encryptWordsIdsDB()

    suspend fun encryptRandomedWordsIdsDB()

    suspend fun checkEncryptedRandomedWordsIdsDB()
}