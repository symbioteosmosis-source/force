pluginManagement {
    repositories {
        google()
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

rootProject.name = "Force"

include(":app")
include(":core-common")
include(":core-designsystem")
include(":core-ui")
include(":core-database")
include(":core-datastore")
include(":core-model")
include(":core-network")
include(":data")
include(":domain")
include(":navigation")
include(":feature-splash")
include(":feature-home")
include(":feature-workout")
include(":feature-progress")
include(":feature-nutrition")
include(":feature-profile")
include(":feature-settings")
include(":feature-ai")
include(":build-logic")
