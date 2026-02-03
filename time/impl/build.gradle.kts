plugins {
    alias(libs.plugins.library.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.gradle)
}

android {
    namespace = "merail.life.time.impl"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.play.services.time)

    implementation(projects.time.api)
    implementation(projects.config.api)
}
