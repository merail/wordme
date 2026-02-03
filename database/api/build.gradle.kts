plugins {
    alias(libs.plugins.library.plugin)
}

android {
    namespace = "merail.life.database.api"
}

dependencies {
    implementation(projects.domain)
}
