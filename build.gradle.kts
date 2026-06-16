plugins {
    glass(JAVA)
    glass(PUBLISHING)
    glass(SIGNING)
    spotless(GRADLE)
    spotless(JAVA)
}

group = "top.nustar.nustarmythicmobsextension"
version = "2.0.11"

allprojects {
    if (!project.buildFile.exists()) {
        return@allprojects
    }

    apply {
        glass(JAVA)
        glass(PUBLISHING)
        glass(SIGNING)
        spotless(GRADLE)
        spotless(JAVA)
    }

    group = rootProject.group
    version = rootProject.version

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
            vendor.set(JvmVendorSpec.AZUL)
        }
    }

    glass {
        release.set(8)

        application {
            sugar {
                enabled.set(true)
            }
        }

        withCopyright()
        withMavenPom()

        withSourcesJar()
        withJavadocJar()

        withInternal()
        withShadow()

        withJUnitTest()
    }

    repositories {
        mavenLocal()
        aliyun()
        sonatype()
        sonatype(SNAPSHOT)
        maven {
            name = "spigotmc-repo"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }
        maven {
            name = "placeholder-api"
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        }
        maven {
            name = "paper"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            name = "nustar-repo"
            url = uri("https://maven.nustar.top/repository/nustar-public/")
        }
        mavenCentral()
    }

    dependencies {
        if (project.name == rootProject.name) {
            implementation(rootProject.libs.nms)
        } else {
            compileOnly(rootProject.libs.nms)
        }
        if (project.name.startsWith("${rootProject.name}-mythicmobs-adapter-")) {
            implementation(rootProject.project(":${rootProject.name}-plugin"))
            implementation(rootProject.project(":${rootProject.name}-adapter-common"))
            implementation(rootProject.project(":${rootProject.name}-adapter-api"))
            implementation(rootProject.project(":${rootProject.name}-api"))
        }
        compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
        @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
        compileOnly(rootProject.libs.spigot.api)
        compileOnly(rootProject.libs.placeholderapi)
        compileOnly(rootProject.libs.attributeplus)
        compileOnly(rootProject.libs.sxattribute)
        compileOnly(rootProject.libs.minecraft.next.spigot)
        compileOnly(fileTree(File(projectDir, "libraries")))

        @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
        testImplementation(rootProject.libs.spigot.api)
        testImplementation(rootProject.libs.minecraft.next.spigot)

        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
        testCompileOnly(rootProject.libs.lombok)
        testAnnotationProcessor(rootProject.libs.lombok)
    }

    tasks.processResources {
        val props =
            mapOf(
                "version" to project.version,
            )
        filesMatching(listOf("plugin.yml")) {
            expand(props)
        }
    }

    publishing {
        repositories {
            project(project)
        }
    }
}

dependencies {
    internal("org.bstats:bstats-bukkit:3.0.2")
    subprojects {
        shadow(rootProject.project(path))
    }
}
