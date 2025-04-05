import org.gradle.plugins.ide.idea.model.IdeaModel

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

    configure<IdeaModel> {
        module.isDownloadJavadoc = true
        module.isDownloadSources = true
    }

    tasks.withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
}

