import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.0.20"
    id("fabric-loom") version "1.8.11"
    kotlin("plugin.serialization") version "2.0.20"
}

group = "de.royzer"
version = "1.0"

val silkVersion = "1.10.7"
val minecraftVersion = "1.21.1"
val cloudNetVersion = "4.0.0-RC10"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.parchmentmc.org/")
    maven("https://repo.cloudnetservice.eu/repository/releases/")
}

val transitiveInclude: Configuration by configurations.creating { }

dependencies {

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21:2024.07.28@zip")
//        parchment("org.parchmentmc.data:parchment-1.20.2:2023.10.08@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:0.16.5")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.103.0+1.21.1")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.12.1+kotlin.2.0.20")
    modImplementation("net.silkmc:silk-core:$silkVersion")
    modImplementation("net.silkmc:silk-commands:$silkVersion")
    modImplementation("net.silkmc:silk-igui:$silkVersion")
    modImplementation("net.silkmc:silk-persistence:$silkVersion")
    modImplementation("net.silkmc:silk-nbt:$silkVersion")
    modImplementation("net.silkmc:silk-network:$silkVersion")
    modImplementation("net.silkmc:silk-game:$silkVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    transitiveInclude(implementation(platform("org.dizitart:nitrite-bom:4.2.2"))!!)
    transitiveInclude(implementation ("org.dizitart:potassium-nitrite")!!)
    transitiveInclude(implementation ("org.dizitart:nitrite-mvstore-adapter")!!)

    modCompileOnly("eu.cloudnetservice.cloudnet:driver:$cloudNetVersion")
    modCompileOnly("eu.cloudnetservice.cloudnet:bridge:$cloudNetVersion")
    modCompileOnly("eu.cloudnetservice.cloudnet:wrapper-jvm:$cloudNetVersion")

    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
    }
}

loom {
    serverOnlyMinecraftJar()
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
//        kotlinOptions.jvmTarget = "21"
    }
    withType<JavaCompile> {
        options.compilerArgs.add("--enable-preview") // danke trymacs
        options.encoding = "UTF-8"
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}
