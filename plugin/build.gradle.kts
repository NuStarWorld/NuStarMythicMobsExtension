import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Jar
import team.idealstate.glass.plugin.java.task.RelocateTask

val bundledDependencies by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations.named("compileOnly") {
    extendsFrom(bundledDependencies)
}

dependencies {
    implementation(rootProject.project(":${rootProject.name}-api"))
    implementation(rootProject.project(":${rootProject.name}-adapter-api"))

    bundledDependencies("org.bstats:bstats-bukkit:3.0.2")
}

val bundledDependenciesDirectory = layout.buildDirectory.dir("dependencies/bundled")
val unpackBundledDependencies by tasks.registering(Sync::class) {
    from(
        bundledDependencies.incoming.files.elements.map { artifacts ->
            artifacts.map { zipTree(it.asFile) }
        },
    )
    into(bundledDependenciesDirectory)
    exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/maven/**")
}

tasks.named<RelocateTask>("relocate") {
    dependsOn(unpackBundledDependencies)
    source(bundledDependenciesDirectory)
    relocator("org/bstats", "${project.group.toString().replace('.', '/')}/internal/org/bstats")
}

tasks.named<Jar>("jar") {
    from(unpackBundledDependencies) {
        includeEmptyDirs = false
    }
}
