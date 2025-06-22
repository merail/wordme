package merail.life.config.api

interface IConfigRepository {
    suspend fun fetchAndActivateRemoteConfig()

    fun gameCountdownStartDate(): String
}