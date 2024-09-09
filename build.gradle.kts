import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.0.0"
    id("io.izzel.taboolib") version "2.0.12"
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
            install("basic-configuration")

            install(
                "bukkit-fake-op",
                "bukkit-hook",
                "bukkit-nms",
                "bukkit-nms-stable",
                "bukkit-ui",
                "bukkit-util",
                "bukkit-xseries",
                "bukkit-xseries-item"
            )

            install(
                "database"
            )

            install(
                "minecraft-chat",
                "minecraft-i18n",
                "minecraft-kether",
                "minecraft-metrics"
            )

            install(
                "platform-bukkit",
                "platform-bukkit-impl"
            )

            install(
                "script-javascript",
                "script-jexl"
            )

            repoTabooLib = "http://sacredcraft.cn:8081/repository/releases/"
        }
        version {
            taboolib = "6.2.0-beta1-dev"
            coroutines = null
        }
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("http://sacredcraft.cn:8081/repository/releases？") { isAllowInsecureProtocol = true }
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://repo.opencollab.dev/main/")
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
            freeCompilerArgs.addAll(
                listOf(
                    "-Xskip-prerelease-check",
                    "-Xallow-unstable-dependencies"
                )
            )
        }
    }

    // Java 版本设置
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
