rootProject.name = "FabricHG"

include("core")

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://server.bbkr.space/artifactory/libs-release/")
        maven("https://maven.quiltmc.org/repository/release/")
        gradlePluginPortal()
    }
}