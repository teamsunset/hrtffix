import club.redux.sunset.hrtffix.Props
import org.gradle.plugins.ide.idea.model.IdeaModel
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
    id("java")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

subprojects {
    plugins.apply("java")
    plugins.apply("eclipse")
    plugins.apply("idea")
    plugins.apply("maven-publish")
    plugins.apply("java-library")
    plugins.apply("org.jetbrains.gradle.plugin.idea-ext")

    configure<JavaPluginExtension> { toolchain.languageVersion.set(JavaLanguageVersion.of(17)) }

    repositories {
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://jitpack.io")
        maven("https://maven.terraformersmc.com/releases/")
        maven { url = uri("https://www.cursemaven.com"); content { includeGroup("curse.maven") } }
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<Jar>().configureEach {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to Props.MOD_ID,
                    "Specification-Vendor" to Props.MOD_AUTHORS,
                    "Specification-Version" to "1", // We are version 1 of ourselves
                    "Implementation-Title" to Props.MOD_NAME,
                    "Implementation-Version" to Props.MOD_VERSION,
                    "Implementation-Vendor" to Props.MOD_AUTHORS,
                    "Implementation-Timestamp" to DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                        .format(Date().toInstant().atOffset(ZoneOffset.UTC))
                )
            )
        }
    }

    configure<IdeaModel> {
        module.isDownloadJavadoc = true
        module.isDownloadSources = true
    }

    tasks.withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
}

