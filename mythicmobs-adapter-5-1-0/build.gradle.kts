dependencies {
    compileOnly(rootProject.libs.mythicmobs510)
    testImplementation("org.ow2.asm:asm-tree:9.7.1")
}

// 三版分别读取本模块字节码，不加载或混用 MythicMobs 运行时。
sourceSets.test {
    java.srcDir(rootProject.file("src/testVersion/java"))
}

tasks.test {
    systemProperty("cylinder.version", "5_1_0")
}
