plugins {
    alias(libs.plugins.library.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.room)
}

android {
    namespace = "merail.life.database.impl"

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.android.database.sqlcipher)

    implementation(projects.domain)
    implementation(projects.database.api)
}
