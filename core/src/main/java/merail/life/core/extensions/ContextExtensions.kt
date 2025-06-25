package merail.life.core.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager

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

private const val GMS_PACKAGE_NAME = "com.google.android.gms"
private const val MIN_GMS_VERSION = 214218053
val Context.isActualGmsVersionInstalled: Boolean
    get() = try {
        packageManager.getPackageInfo(GMS_PACKAGE_NAME, 0).longVersionCode > MIN_GMS_VERSION
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        false
    }
