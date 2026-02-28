package merail.life.core.log

import android.util.Log
import javax.inject.Inject

class WordMeLogger @Inject constructor() : IWordMeLogger {
    override fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    override fun w(tag: String, msg: String, tr: Throwable?) {
        Log.w(tag, msg, tr)
    }
}
