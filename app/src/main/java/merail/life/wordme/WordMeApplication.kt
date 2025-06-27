package merail.life.wordme

import android.app.Application
import com.google.android.gms.time.TrustedTime
import dagger.hilt.android.HiltAndroidApp
import merail.life.core.extensions.isActualGmsVersionInstalled
import merail.life.time.api.ITimeSource
import javax.inject.Inject

@HiltAndroidApp
internal class WordMeApplication : Application() {

    @Inject
    lateinit var timeSource: ITimeSource

    override fun onCreate() {
        super.onCreate()

        if (isActualGmsVersionInstalled) {
            initTrustedTimeClient()
        } else {
            timeSource.setTimeTrustedClient(null)
        }
    }

    private fun initTrustedTimeClient() = TrustedTime
        .createClient(this)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                timeSource.setTimeTrustedClient(task.result)
            }
        }
}