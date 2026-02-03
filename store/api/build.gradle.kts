plugins {
    alias(libs.plugins.library.plugin)
}

android {
    namespace = "merail.life.store.api"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(projects.domain)
}
