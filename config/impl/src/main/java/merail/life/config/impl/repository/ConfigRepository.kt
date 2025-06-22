package merail.life.config.impl.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import merail.life.config.api.IConfigRepository
import javax.inject.Inject

internal class ConfigRepository @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : IConfigRepository {

    companion object {
        private const val GAME_COUNTDOWN_START_DATE = "gameCountdownStartDate"
    }

    override suspend fun fetchAndActivateRemoteConfig() {
        withContext(Dispatchers.IO) {
            firebaseRemoteConfig.fetchAndActivate().await()
        }
    }

    override fun gameCountdownStartDate() = firebaseRemoteConfig.getString(GAME_COUNTDOWN_START_DATE)
}