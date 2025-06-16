package merail.life.wordme

import android.app.Application
import com.google.android.gms.time.TrustedTime
import dagger.hilt.android.HiltAndroidApp
import merail.life.time.api.ITimeRepository
import javax.inject.Inject

@HiltAndroidApp
internal class WordMeApplication : Application() {

    @Inject
    lateinit var timeRepository: ITimeRepository

    override fun onCreate() {
        super.onCreate()

        initTrustedTimeClient()
    }

    private fun initTrustedTimeClient() = TrustedTime
        .createClient(this)
        .addOnCompleteListener { task ->
            task.exception?.printStackTrace()
            if (task.isSuccessful) {
                timeRepository.setTimeTrustedClient(task.result)
            }
        }
}