package merail.life.config.impl.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import merail.life.config.api.IConfigRepository
import javax.inject.Inject

internal class ConfigRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : IConfigRepository {

    override suspend fun authAnonymously() {
        withContext(Dispatchers.IO) {
            auth.signInAnonymously().await()
        }
    }
}