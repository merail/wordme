plugins {
    alias(libs.plugins.library.plugin)
}

android {
    namespace = "merail.life.game.api"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)

    implementation(projects.domain)
}
