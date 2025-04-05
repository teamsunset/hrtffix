import club.redux.sunset.hrtffix.Props
import club.redux.sunset.hrtffix.api.toMap

val neoforgeVersion: String by project
val neoforgeVersionRange: String by project
val modLoader: String by project
val modLoaderVersionRange: String by project
val minecraftMappingChannel: String by project
val minecraftMappingMinecraftVersion: String by project
val minecraftMappingVersion: String by project

version = "neoforge-${Props.MOD_VERSION}"
group = Props.MOD_GROUP_ID
base.archivesName.set(Props.MOD_ID)

idea.module.isDownloadJavadoc = true
idea.module.isDownloadSources = true

plugins {
    java
    eclipse
    idea
    `maven-publish`
    `java-library`
    id("net.neoforged.gradle.userdev") version "7.0.145"
    id("net.neoforged.gradle.mixin") version "7.0.145"
}

tasks.compileJava { source(project(":common").sourceSets["main"].allSource) }
evaluationDependsOn(":common")

dependencies {
    val mixinProcessor = "org.spongepowered:mixin:0.8.5:processor"

    // NeoForge
    implementation("net.neoforged:neoforge:${neoforgeVersion}")

    compileOnly(project(":common"))

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")

    // Mixin
    annotationProcessor(mixinProcessor)
}

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")
        modSource(sourceSets["main"])
    }

    named("client") { systemProperty("forge.enabledGameTestNamespaces", Props.MOD_ID) }
    named("server") { systemProperty("forge.enabledGameTestNamespaces", Props.MOD_ID) }
    named("gameTestServer") { systemProperty("forge.enabledGameTestNamespaces", Props.MOD_ID) }
    named("data") {
        programArguments.addAll(
            "--mod",
            Props.MOD_ID,
            "--all",
            "--output",
            file("src/generated/resources/").absolutePath,
            "--existing",
            file("src/main/resources/").absolutePath
        )
    }
}

minecraft {
//    accessTransformers { file("src/main/resources/META-INF/accesstransformer.cfg") }
}
mixin {
//    config("${Props.MOD_ID}.mixins.json")
}

tasks.compileJava { options.annotationProcessorPath = files() }

subsystems {
    parchment.let {
        it.minecraftVersion = minecraftMappingMinecraftVersion
        it.mappingsVersion = minecraftMappingVersion
    }
}

val props = mapOf(
    "neoforge_version" to neoforgeVersion,
    "neoforge_version_range" to neoforgeVersionRange,
    "mod_loader" to modLoader,
    "mod_loader_version_range" to modLoaderVersionRange,
) + Props.toMap()

tasks.processResources {
    val targets = listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta")
    inputs.properties(props)

    from("${project(":common").projectDir}/src/main/resources")

    filesMatching(targets) { expand(props) }
}
