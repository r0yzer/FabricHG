plugins {
    java
    kotlin("jvm") version "1.9.23"
    id("fabric-loom") version "1.5-SNAPSHOT"
    kotlin("plugin.serialization") version "1.8.22"
}

group = "de.royzer"
version = "1.0"

val silkVersion = "1.10.3"
val minecraftVersion = "1.20.4"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.parchmentmc.org/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        parchment("org.parchmentmc.data:parchment-1.20.4:2024.02.25@zip")
        officialMojangMappings()
    })
    modImplementation("net.fabricmc:fabric-loader:0.15.7")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.96.4+1.20.4")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.10.19+kotlin.1.9.23")
    modImplementation("net.silkmc:silk-core:$silkVersion")
    modImplementation("net.silkmc:silk-commands:$silkVersion")
    modImplementation("net.silkmc:silk-igui:$silkVersion")
    modImplementation("net.silkmc:silk-persistence:$silkVersion")
    modImplementation("net.silkmc:silk-nbt:$silkVersion")
    modImplementation("net.silkmc:silk-network:$silkVersion")
    modImplementation("net.silkmc:silk-game:$silkVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

loom {
    serverOnlyMinecraftJar()
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}
