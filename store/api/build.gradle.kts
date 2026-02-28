plugins {
    alias(libs.plugins.library.plugin)
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(projects.domain)
}
