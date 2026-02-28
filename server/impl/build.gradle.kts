import java.util.Properties

plugins {
    alias(libs.plugins.library.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.gradle)
}

android {
    namespace = "merail.life.server.impl"

    buildFeatures {
        buildConfig = true
    }

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "DOMAIN_URL",
            value = "\"${properties.getProperty("domainUrl")}\"",
        )

        buildConfigField(
            type = "String",
            name = "ACCESS_TOKEN",
            value = "\"${properties.getProperty("accessToken")}\"",
        )
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(projects.domain)
    implementation(projects.server.api)
}
