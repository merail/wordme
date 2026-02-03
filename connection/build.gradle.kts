plugins {
    alias(libs.plugins.library.plugin)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "merail.life.connection"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.crashlytics)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.design)
    implementation(projects.domain)
    implementation(projects.core)
    implementation(projects.database.api)
    implementation(projects.store.api)
    implementation(projects.time.api)
    implementation(projects.config.api)
    implementation(projects.game.api)
}
