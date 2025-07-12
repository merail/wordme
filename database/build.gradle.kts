import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.room)
}

android {
    namespace = "merail.life.database"

    compileSdk = 35

    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    hilt {
        enableAggregatingTask = false
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    buildFeatures {
        buildConfig = true
    }

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())

    buildTypes {
        debug {
            buildConfigField(
                type = "String",
                name = "WORDS_ENCRYPTED_DATABASE_PASSPHRASE",
                value = properties.getProperty("wordsEncryptedDatabasePassphrase"),
            )

            buildConfigField(
                type = "String",
                name = "WORDS_IDS_ENCRYPTED_DATABASE_PASSPHRASE",
                value = properties.getProperty("wordsIdsEncryptedDatabasePassphrase"),
            )
        }
    }
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.android.database.sqlcipher)
}