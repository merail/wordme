plugins {
    alias(libs.plugins.library.plugin)
}

android {
    namespace = "merail.life.config.api"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
