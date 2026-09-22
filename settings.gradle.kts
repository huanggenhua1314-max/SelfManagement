pluginManagement {
    repositories {
        google ()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.maven.apache.org/maven2")
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SelfManagement"
include(":shared")
include(":androidApp")
project(":androidApp").projectDir = file("app")
 