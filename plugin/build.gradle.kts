dependencies {
    implementation(rootProject.project(":${rootProject.name}-api"))
    implementation(rootProject.project(":${rootProject.name}-adapter-api"))

    internal("org.bstats:bstats-bukkit:3.0.2")
}
