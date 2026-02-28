plugins {
    alias(libs.plugins.library.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.gradle)
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.kotlinx.coroutines.android)

    implementation(projects.domain)
    implementation(projects.config.api)
}
