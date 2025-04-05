plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.parchmentmc.org")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
}
