package merail.life.config.impl.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import merail.life.config.api.IConfigRepository
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.domain.exceptions.TestFirebaseException
import javax.inject.Inject

internal class ConfigRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : IConfigRepository {

    companion object {
        internal const val CONFIG_KEY = "config"
        internal const val VALUE_KEY = "value"

        internal const val IDS_DATABASE_PASSWORD_KEY = "idsDatabasePassword"
        internal const val GAME_COUNTDOWN_START_DATE_KEY = "gameCountdownStartDate"
    }

    private var idsDatabasePassword = MutableStateFlow("")

    private var gameCountdownStartDate = MutableStateFlow("")

    private val Exception.isFirebaseException: Boolean
        get() = this is FirebaseNetworkException
                || this is FirebaseFirestoreException
                || this is TestFirebaseException

    override suspend fun authAnonymously() {
        with(Dispatchers.IO) {
            try {
                auth.signInAnonymously().await()

            } catch (e: Exception) {
                throw if (e.isFirebaseException) {
                    NoInternetConnectionException()
                } else {
                    e
                }
            }
        }
    }

    override suspend fun fetchInitialValues() {
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
                throw if (e.isFirebaseException) {
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