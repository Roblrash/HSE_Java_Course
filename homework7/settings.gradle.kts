pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "homework7"

include("orm")
include("homework-orm-6")
include("homework-orm-8")
include("homework-orm-9")
include("homework-orm-10")
