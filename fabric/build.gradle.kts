import club.redux.sunset.hrtffix.Props
import club.redux.sunset.hrtffix.api.toMap

version = "fabric-${Props.MOD_VERSION}"
group = Props.MOD_GROUP_ID
base.archivesName.set(Props.MOD_ID)

plugins {
    id("fabric-loom") version ("1.4.4")
}

tasks.compileJava { source(project(":common").sourceSets["main"].allSource) }
evaluationDependsOn(":common")

dependencies {
    minecraft("net.minecraft:minecraft:${Props.MINECRAFT_VERSION}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.15.0")
    modImplementation("com.terraformersmc:modmenu:8.0.0")
    compileOnly(project(":common"))
}

val props = Props.toMap()

tasks.processResources {
    val targets = listOf("fabric.mod.json")
    inputs.properties(props)

    from("${project(":common").projectDir}/src/main/resources")

    filesMatching(targets) {
        expand(props)
    }
}
