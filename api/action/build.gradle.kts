val taboolibVersion: String by rootProject

plugins {
    id("io.izzel.taboolib")
}

taboolib {
    description {
        name(rootProject.name)
    }
    install(
        "common",
        "common-5",
        "expansion-javascript",
        "module-chat",
        "module-configuration",
    )
    options(
        "skip-minimize",
        "keep-kotlin-module",
        "skip-taboolib-relocate",
    )
    classifier = null
    version = taboolibVersion
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.tabooproject.org/repository/releases")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    api(project(":common"))

    compileOnly("net.md-5:bungeecord-chat:1.20-R0.2-SNAPSHOT") // For compatibility
}

tasks.tabooRelocateJar { onlyIf { false } }