import java.util.Properties

plugins {
    alias(libs.plugins.library.plugin)
}

android {
    namespace = "merail.life.time.api"

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
        }
    }

    buildTypes {
        release {
            buildConfigField(
                type = "Boolean",
                name = "REDUCE_TIME_UNTIL_NEXT_DAY",
                value = "false",
            )
        }
    }
}

dependencies {
    implementation(libs.play.services.time)

    implementation(libs.kotlinx.coroutines.android)
}
