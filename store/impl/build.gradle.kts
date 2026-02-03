import com.google.protobuf.gradle.GenerateProtoTask

plugins {
    alias(libs.plugins.library.plugin)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "merail.life.store.impl"
}

dependencies {
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.protobuf.javalite)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.androidx.preference.ktx)

    implementation(projects.domain)
    implementation(projects.store.api)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            val protoTask =
                project.tasks.getByName("generate" + variant.name.replaceFirstChar { it.uppercaseChar() } + "Proto") as GenerateProtoTask

            project.tasks.getByName("ksp" + variant.name.replaceFirstChar { it.uppercaseChar() } + "Kotlin") {
                dependsOn(protoTask)
                (this as org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool<*>).setSource(
                    protoTask.outputBaseDir
                )
            }
        }
    }
}
