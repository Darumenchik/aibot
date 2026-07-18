rootProject.name = "Chiper"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://maven.pkg.jetbrains.space/public/p/compose/compose") }
    }
}

include(":androidApp")
include(":shared")
