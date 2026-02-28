plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("library-plugin") {
            id = "convention.library.plugin"
            implementationClass = "LibraryConventionPlugin"
        }
    }
}
