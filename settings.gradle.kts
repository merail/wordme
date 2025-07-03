pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WordMe"
include(":app")
include(":core")
include(":design")
include(":navigation")
include(":navigation:graph")
include(":navigation:domain")
include(":game")
include(":game:api")
include(":game:impl")
include(":database")
include(":database:api")
include(":database:impl")
include(":store")
include(":store:api")
include(":store:impl")
include(":domain")
include(":result")
include(":stats")
include(":time")
include(":time:api")
include(":time:impl")
include(":config")
include(":config:api")
include(":config:impl")
include(":connection")
include(":profiling")
