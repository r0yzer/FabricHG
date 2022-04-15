plugins {
    java
    kotlin("jvm") version "1.6.20"
    id("fabric-loom") version "0.11-SNAPSHOT"
    id("org.quiltmc.quilt-mappings-on-loom") version "4.0.0"
    kotlin("plugin.serialization") version "1.6.20"
}

group = "de.royzer"
version = "1.0"

val fabrikVersion = "1.7.2"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft("com.mojang:minecraft:1.18.2")
    mappings(loom.layered {
        addLayer(quiltMappings.mappings("org.quiltmc:quilt-mappings:1.18.2+build.22:v2"))
        officialMojangMappings()
    })
    modImplementation("net.fabricmc:fabric-loader:0.13.3")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.50.0+1.18.2")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.7.3+kotlin.1.6.20")
    modImplementation("net.axay:fabrikmc-core:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-commands:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-igui:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-persistence:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-nbt:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-network:$fabrikVersion")
    modImplementation("net.axay:fabrikmc-game:$fabrikVersion")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "18"
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

gradle.buildFinished {
    Runtime.getRuntime().exec("scp ./build/libs/FabricHG-1.0.jar royzer@royzer.de:/home/royzer/minecraft/fabric/mods/FabricHG-1.0.jar")
}