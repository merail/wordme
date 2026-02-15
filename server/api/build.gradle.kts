plugins {
    alias(libs.plugins.library.plugin)
}

android {
    namespace = "merail.life.server.api"
}

dependencies {
    implementation(projects.domain)
}
