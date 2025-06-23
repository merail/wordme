package merail.life.config.api

interface IConfigRepository {
    suspend fun fetchAndActivateRemoteConfig()

    fun getIdsDatabasePassword(): String

    fun getGameCountdownStartDate(): String
}