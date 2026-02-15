package merail.life.config.api

interface IConfigRepository {
    suspend fun authAnonymously()
}