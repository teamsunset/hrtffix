import club.redux.sunset.hrtffix.Props
import club.redux.sunset.hrtffix.api.toMap
import net.minecraftforge.gradle.common.util.RunConfig

val forgeVersion: String by project
val forgeVersionRange: String by project
val minecraftMappingChannel: String by project
val minecraftMappingVersion: String by project
val modLoader: String by project
val modLoaderVersionRange: String by project
val minecraftVersion = "1.20.6"

version = "forge-1.21.x-${Props.MOD_VERSION}"
group = Props.MOD_GROUP_ID
base.archivesName.set(Props.MOD_ID)

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

plugins {
    java
    eclipse
    idea
    `maven-publish`
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"
}

repositories {
    maven("https://maven.aliyun.com/repository/public/")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://jitpack.io")
    maven { url = uri("https://www.cursemaven.com");content { includeGroup("curse.maven") } }
    mavenLocal()
    mavenCentral()
}

tasks.compileJava { source(project(":common").sourceSets["main"].allSource) }
evaluationDependsOn(":common")

dependencies {
    val mc = "net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}"
    val mixinProcessor = "org.spongepowered:mixin:0.8.5:processor"

    compileOnly(project(":common"))

    // Minecraft
    minecraft(mc)

    // Mixin
    annotationProcessor(mixinProcessor)
}

mixin {
    add(sourceSets.main.get(), "${Props.MOD_ID}.refmap.json")
    config("${Props.MOD_ID}.mixins.json")
}
tasks.compileJava { options.annotationProcessorPath = files() }

minecraft {
    mappings(minecraftMappingChannel, minecraftMappingVersion)

    copyIdeResources = true

//    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    fun createMinecraftRun(vararg names: String, additionalConfig: RunConfig.() -> Unit = { }) {
        for (name in names) {
            minecraft.runs.create(name) {
                workingDirectory(project.file("run"))

                property("forge.logging.markers", "REGISTRIES")
                property("forge.logging.console.level", "debug")

                this.additionalConfig()

                mods {
                    create(Props.MOD_ID) {
                        source(sourceSets["main"])
                    }
                }
            }
        }
    }

    createMinecraftRun("client", "server", "gameTestServer") {
        property("forge.enabledGameTestNamespaces", Props.MOD_ID)
    }

    createMinecraftRun("data") {
        args(
            "--mod",
            Props.MOD_ID,
            "--all",
            "--output",
            file("src/generated/resources/"),
            "--existing",
            file("src/main/resources/")
        )
    }
}

val props = (mapOf(
    "forge_version" to forgeVersion,
    "forge_version_range" to forgeVersionRange,
    "mod_loader" to modLoader,
    "mod_loader_version_range" to modLoaderVersionRange,
) + Props.toMap()).toMutableMap().apply {
    put("minecraft_version", "1.20.6")
    put("minecraft_version_range", "[1.20.6,1.21.5)")
}

tasks.processResources {
    val targets = listOf("META-INF/mods.toml", "pack.mcmeta")
    inputs.properties(props)

    from("${project(":common").projectDir}/src/main/resources")

    filesMatching(targets) { expand(props) }
}
