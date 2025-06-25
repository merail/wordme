package merail.life.wordme

import android.app.Application
import com.google.android.gms.time.TrustedTime
import dagger.hilt.android.HiltAndroidApp
import merail.life.core.extensions.isActualGmsVersionInstalled
import merail.life.time.api.ITimeRepository
import javax.inject.Inject

@HiltAndroidApp
internal class WordMeApplication : Application() {

    @Inject
    lateinit var timeRepository: ITimeRepository

    override fun onCreate() {
        super.onCreate()

        if (isActualGmsVersionInstalled) {
            initTrustedTimeClient()
        } else {
            timeRepository.setCurrentUnixEpochMillis(System.currentTimeMillis())
        }
    }

    private fun initTrustedTimeClient() = TrustedTime
        .createClient(this)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                timeRepository.setCurrentUnixEpochMillis(task.result.computeCurrentUnixEpochMillis())
            }
        }
}