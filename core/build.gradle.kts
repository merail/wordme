plugins {
    alias(libs.plugins.library.plugin)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
