package merail.life.config.impl.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import merail.life.config.api.IConfigRepository
import merail.life.domain.exceptions.NoInternetConnectionException
import javax.inject.Inject

internal class ConfigRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : IConfigRepository {

    companion object {
        private const val CONFIG_KEY = "config"
        private const val VALUE_KEY = "value"

        private const val IDS_DATABASE_PASSWORD_KEY = "idsDatabasePassword"
        private const val GAME_COUNTDOWN_START_DATE_KEY = "gameCountdownStartDate"
    }

    private var idsDatabasePassword = MutableStateFlow("")

    private var gameCountdownStartDate = MutableStateFlow("")

    override suspend fun fetchAndActivateRemoteConfig() {
        withContext(Dispatchers.IO) {
            try {
                idsDatabasePassword.value = firestore
                    .collection(CONFIG_KEY)
                    .document(IDS_DATABASE_PASSWORD_KEY)
                    .get()
                    .await()
                    .getString(VALUE_KEY).orEmpty()

                gameCountdownStartDate.value = firestore
                    .collection(CONFIG_KEY)
                    .document(GAME_COUNTDOWN_START_DATE_KEY)
                    .get()
                    .await()
                    .getString(VALUE_KEY).orEmpty()
            } catch (e: Exception) {
                throw if (e is FirebaseFirestoreException) {
                    NoInternetConnectionException()
                } else {
                    e
                }
            }
        }
    }

    override fun getIdsDatabasePassword() = idsDatabasePassword

    override fun getGameCountdownStartDate() = gameCountdownStartDate
}