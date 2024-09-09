import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "2.0.0"
    id("io.izzel.taboolib") version "2.0.17"
}

// 这段。一言难尽，但我不想动 (依托)
tasks.build {
    doLast {
        val plugin = project(":plugin")
        val file =
            file("${plugin.layout.buildDirectory.get()}/libs").listFiles()?.find { it.endsWith("plugin-$version.jar") }

        file?.copyTo(file("${project.layout.buildDirectory.get()}/libs/${project.name}-$version.jar"), true)
    }
    dependsOn(project(":plugin").tasks.build)
}

subprojects {

    apply<JavaPlugin>()
    apply(plugin = "io.izzel.taboolib")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    taboolib {
        env {
            install(
                UNIVERSAL, DATABASE, KETHER, METRICS, NMS, NMS_UTIL, UI,
                CHAT,
                EXPANSION_REDIS, EXPANSION_JAVASCRIPT,EXPANSION_JEXL, EXPANSION_PLAYER_DATABASE, EXPANSION_PLAYER_FAKE_OP,
                BUKKIT_ALL,EXPANSION_FOLIA
            )
        }
        version {
            taboolib = "6.1.2-beta10"
            coroutines = null
        }
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("http://sacredcraft.cn:8081/repository/releases") { isAllowInsecureProtocol = true }
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://repo.opencollab.dev/main/")
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs += listOf("-Xskip-prerelease-check","-Xallow-unstable-dependencies")
        }
    }

    // Java 版本设置
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}
