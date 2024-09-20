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
        maven {
            url = uri("https://artifactory.nb-sandbox.com/artifactory/libs-release-local")
            credentials {
                username = "services"
                password = "services"
            }
        }
        // Define the flatDir repository to include .aar and .jar files from the libs folder
        flatDir {
            dirs("app/libs")
        }
    }
}

rootProject.name = "msp-demo"
include(":app")
