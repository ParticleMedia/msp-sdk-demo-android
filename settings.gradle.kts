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
        // Define the flatDir repository to include .aar and .jar files from the libs folder
        flatDir {
            dirs("app/libs")
        }
    }
}

rootProject.name = "msp-demo"
include(":app")
