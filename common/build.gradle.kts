import club.redux.sunset.hrtffix.Props
import club.redux.sunset.hrtffix.api.toMap
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

version = Props.MOD_VERSION
group = Props.MOD_GROUP_ID
base.archivesName.set("${Props.MOD_ID}-common")

plugins {
    id("fabric-loom") version ("1.4.4")
}

dependencies {
    minecraft("net.minecraft:minecraft:${Props.MINECRAFT_VERSION}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.14.0")
}

val props = Props.toMap()

val generateTemplates by tasks.registering(Copy::class) {
    val src = file("src/main/templates/")
    val dst = layout.buildDirectory.dir("generated/sources/templates/")
    inputs.properties(props)

    from(src)
    into(dst)
    expand(props)
}
sourceSets["main"].java.srcDirs(generateTemplates.map { it.destinationDir }.map {
    listOf("java", "kotlin").map(it::resolve)
})
rootProject.idea.project.settings.taskTriggers.afterSync(generateTemplates)
project.eclipse.synchronizationTasks(generateTemplates)
