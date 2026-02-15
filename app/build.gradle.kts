import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "merail.life.wordme"

    compileSdk = 36

    defaultConfig {
        applicationId = "merail.life.wordme"
        minSdk = 30
        targetSdk = 36
        versionCode = (project.findProperty("VERSION_CODE") as String?)?.toIntOrNull() ?: 1
        versionName = project.findProperty("VERSION_NAME") as String? ?: "1.0"
        testInstrumentationRunner = "merail.life.wordme.HiltTestRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("RELEASE_KEYSTORE_FILE")

            if (keystorePath != null) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            } else {
                val localProperties = gradleLocalProperties(rootDir, providers)

                storeFile = file("keystore.keystore")
                storePassword = localProperties.getProperty("releaseKeystorePassword")
                keyAlias = localProperties.getProperty("releaseKeystoreAlias")
                keyPassword = localProperties.getProperty("releaseKeyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true

            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.target
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.profileinstaller)
    baselineProfile(projects.profiling)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.uiautomator)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.navigation.testing)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.play.services.time)

    implementation(projects.core)
    implementation(projects.design)
    implementation(projects.domain)
    implementation(projects.game.api)
    implementation(projects.game.impl)
    implementation(projects.server.api)
    implementation(projects.server.impl)
    implementation(projects.store.api)
    implementation(projects.store.impl)
    implementation(projects.result)
    implementation(projects.stats)
    implementation(projects.time.api)
    implementation(projects.time.impl)
    implementation(projects.config.api)
    implementation(projects.config.impl)
    implementation(projects.connection)
}