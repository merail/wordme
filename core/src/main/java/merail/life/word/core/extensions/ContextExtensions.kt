package merail.life.word.core.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

val Context.activity: Activity?
    get() {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

val Context.isNavigationBarEnabled: Boolean
    @SuppressLint("DiscouragedApi")
    get() {
        val navigationBarModeId = resources.getIdentifier(
            "config_navBarInteractionMode",
            "integer",
            "android",
        )
        val navigationBarMode = resources.getInteger(navigationBarModeId)
        return navigationBarMode != 2
    }