import io.izzel.taboolib.gradle.*

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    id("io.izzel.taboolib") version "2.0.11"
}

// 这段。一言难尽，但我不想动
tasks.build {
    doLast {
        val plugin = project(":plugin")
        val file = file("${plugin.layout.buildDirectory.get()}/libs").listFiles()?.find { it.endsWith("plugin-$version.jar") }

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
                EXPANSION_REDIS, EXPANSION_JAVASCRIPT, EXPANSION_PLAYER_DATABASE,
                BUKKIT_ALL
            )
        }
        version {
            taboolib = "6.1.1-beta24"
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

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    // Java 版本设置
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}
