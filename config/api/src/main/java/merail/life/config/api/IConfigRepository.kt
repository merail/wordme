package merail.life.config.api

import kotlinx.coroutines.flow.Flow

interface IConfigRepository {
    suspend fun fetchAndActivateRemoteConfig()

    fun getIdsDatabasePassword(): Flow<String>

    fun getGameCountdownStartDate(): Flow<String>
}