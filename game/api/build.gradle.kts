plugins {
    alias(libs.plugins.library.plugin)
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)

    implementation(projects.domain)
}
