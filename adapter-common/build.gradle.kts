dependencies {
    implementation(rootProject.project(":${rootProject.name}-plugin"))
    implementation(rootProject.project(":${rootProject.name}-api"))
    implementation(rootProject.project(":${rootProject.name}-adapter-api"))
    testImplementation("org.ow2.asm:asm-tree:9.7.1")
}
