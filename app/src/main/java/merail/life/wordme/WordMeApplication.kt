package merail.life.wordme

import android.app.Application
import com.google.android.gms.time.TrustedTime
import dagger.hilt.android.HiltAndroidApp
import merail.life.core.extensions.isActualGmsVersionInstalled
import merail.life.core.log.IWordMeLogger
import merail.life.time.api.BuildConfig
import merail.life.time.api.ITimeSource
import javax.inject.Inject

@HiltAndroidApp
internal class WordMeApplication : Application() {

    companion object {
        private const val TAG = "WordMeApplication"
    }

    @Inject
    lateinit var timeSource: ITimeSource

    @Inject
    lateinit var logger: IWordMeLogger

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.USE_TRUSTED_TIME_CLIENT && isActualGmsVersionInstalled) {
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
            } else {
                logger.w(
                    tag = TAG,
                    msg = "TrustedTimeClient creation failed",
                    th = task.exception,
                )
                timeSource.setTimeTrustedClient(null)
            }
        }
}
