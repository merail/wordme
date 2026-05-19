import java.util.Properties

plugins {
    alias(libs.plugins.library.plugin)
}

android {
    buildFeatures {
        buildConfig = true
    }

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())

    buildTypes {
        debug {
            buildConfigField(
                type = "Boolean",
                name = "REDUCE_TIME_UNTIL_NEXT_DAY",
                value = properties.getProperty("reduceTimeUntilNextDay"),
            )
            buildConfigField(
                type = "Boolean",
                name = "USE_TRUSTED_TIME_CLIENT",
                value = properties.getProperty("useTrustedTimeClient"),
            )
        }
    }

    buildTypes {
        release {
            buildConfigField(
                type = "Boolean",
                name = "REDUCE_TIME_UNTIL_NEXT_DAY",
                value = "false",
            )
            buildConfigField(
                type = "Boolean",
                name = "USE_TRUSTED_TIME_CLIENT",
                value = properties.getProperty("useTrustedTimeClient"),
            )
        }
    }
}

dependencies {
    implementation(libs.play.services.time)

    implementation(libs.kotlinx.coroutines.android)
}
