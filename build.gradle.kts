plugins {
    java
    id("fabric-loom") version "0.8-SNAPSHOT"
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
}

group = "de.royzer"
version = "1.0"

val minecraftVersion = "1.17.1"
val yarnMappingsVersion = "1.17.1+build.39:v2"
val fabricLoaderVersion = "0.11.6"
val fabricApiVersion = "0.38.0+1.17"
val fabricLanguageKotlinVersion = "1.6.3+kotlin.1.5.21"
val fabrikVersion = "1.3.0"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappingsVersion")
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion")
    modImplementation("net.axay:fabrikmc-core:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-commands:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-igui:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-persistence:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-nbt:$fabrikVersion")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}
