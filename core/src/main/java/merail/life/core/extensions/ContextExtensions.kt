package merail.life.core.extensions

import android.annotation.SuppressLint
import android.content.Context

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