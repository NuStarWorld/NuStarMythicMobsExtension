rootProject.name = "NuStarMythicMobsExtension"

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            name = "Sonatype-Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
        maven("https://maven.nustar.top/repository/nustar-public/")
        gradlePluginPortal()
    }
}

plugins {
    id("team.idealstate.glass") version "0.1.0-SNAPSHOT"
}

val modules =
    listOf(
        "adapter-api",
        "adapter-common",
        "api",
        "mythicmobs-adapter-4-9-0",
        "mythicmobs-adapter-5-1-0",
        "mythicmobs-adapter-5-6-0",
        "plugin",
    )

include(*modules.map { ":$it" }.toTypedArray())
modules.forEach { module ->
    project(":$module").name = "${rootProject.name}-$module"
}
