plugins {
    glass(JAVA)
    glass(PUBLISHING)
    glass(SIGNING)
    spotless(GRADLE)
    spotless(JAVA)
}

group = "top.nustar.nustarmythicmobsextension"

val baseVersion = providers.gradleProperty("baseVersion").get()
val releaseBuild =
    providers
        .gradleProperty("release")
        .map { it.toBoolean() }
        .orElse(false)
        .get()
val buildNumber =
    providers
        .gradleProperty("buildNumber")
        .orElse(providers.environmentVariable("GITHUB_RUN_NUMBER"))
        .orElse("local")
        .get()
val buildAttempt =
    providers
        .gradleProperty("buildAttempt")
        .orElse(providers.environmentVariable("GITHUB_RUN_ATTEMPT"))
        .orElse("1")
        .get()
val gitCommit =
    providers
        .gradleProperty("gitCommit")
        .orElse(providers.environmentVariable("GITHUB_SHA"))
        .orElse(
            providers.provider {
                providers
                    .exec {
                        commandLine("git", "rev-parse", "--short=8", "HEAD")
                        isIgnoreExitValue = true
                    }.standardOutput.asText
                    .get()
                    .trim()
            },
        ).get()
        .ifBlank { "unknown" }
        .take(8)
val buildId = if (buildNumber == "local") "local" else "$buildNumber.$buildAttempt"
val buildVersion = if (releaseBuild) baseVersion else "$baseVersion-dev.$buildId-$gitCommit"

version = buildVersion

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

    // 日常构建不带入全量测试；显式 check/test 仍保留原有完整验证。
    tasks.named("build") {
        setDependsOn(dependsOn.filterNot { it == "check" })
        dependsOn(tasks.named("spotlessCheck"))
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
    subprojects {
        shadow(rootProject.project(path))
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            "Implementation-Version" to buildVersion,
            "Build-Commit" to gitCommit,
            "Build-Number" to buildId,
            "Build-Type" to if (releaseBuild) "release" else "development",
        )
    }
}

val buildMetadataFile = layout.buildDirectory.file("build-metadata.properties")

val writeBuildMetadata by tasks.registering {
    group = "build"
    description = "写入当前构建版本，供 CI 和诊断使用。"
    inputs.properties(
        mapOf(
            "baseVersion" to baseVersion,
            "version" to buildVersion,
            "commit" to gitCommit,
            "buildNumber" to buildId,
            "release" to releaseBuild,
        ),
    )
    outputs.file(buildMetadataFile)

    doLast {
        val output = buildMetadataFile.get().asFile
        output.parentFile.mkdirs()
        output.writeText(
            """
            baseVersion=$baseVersion
            version=$buildVersion
            commit=$gitCommit
            buildNumber=$buildId
            release=$releaseBuild
            """.trimIndent() + System.lineSeparator(),
        )
    }
}

tasks.named("build") {
    dependsOn(writeBuildMetadata)
}

tasks.register("verifyReleaseTag") {
    group = "verification"
    description = "Checks that a release build uses a tag matching baseVersion."

    doLast {
        check(releaseBuild) { "verifyReleaseTag requires -Prelease=true." }
        val releaseTag =
            providers.gradleProperty("releaseTag").orNull
                ?: error("Missing -PreleaseTag (expected v$baseVersion).")
        check(releaseTag.removePrefix("v") == baseVersion) {
            "Release tag '$releaseTag' does not match baseVersion '$baseVersion'."
        }
    }
}
