package merail.life.config.api

import kotlinx.coroutines.flow.Flow

interface IConfigRepository {
    suspend fun authAnonymously()

    suspend fun fetchInitialValues()

    fun getIdsDatabasePassword(): Flow<String>

    fun getGameCountdownStartDate(): Flow<String>
}