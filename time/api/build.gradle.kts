import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "merail.life.time.api"
    compileSdk = 35

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.target
    }

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