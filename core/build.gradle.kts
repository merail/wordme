plugins {
    alias(libs.plugins.library.plugin)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

android {
    namespace = "merail.life.core"
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
