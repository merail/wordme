plugins {
    alias(libs.plugins.library.plugin)
}

android {
    namespace = "merail.life.domain"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
