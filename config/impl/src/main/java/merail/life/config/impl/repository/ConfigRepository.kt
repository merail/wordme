package merail.life.config.impl.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import merail.life.config.api.IConfigRepository
import merail.life.domain.exceptions.NoInternetConnectionException
import merail.life.domain.exceptions.TestFirebaseException
import javax.inject.Inject

internal class ConfigRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : IConfigRepository {

    private val Exception.isFirebaseException: Boolean
        get() = this is FirebaseNetworkException
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
}