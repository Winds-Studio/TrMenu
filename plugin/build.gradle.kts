val taboolibVersion: String by rootProject

plugins {
    id("io.izzel.taboolib")
}

taboolib {
    install(
        "common",
        "common-5",
        "expansion-javascript",
        "module-kether",
        "module-ui",
        "module-lang",
        "module-database",
        "module-database-mongodb",
        "module-metrics",
        "module-nms",
        "module-chat",
        "module-nms-util",
        "module-configuration",
        "platform-bukkit"
    )

    description {
        name(rootProject.name)
        contributors {
            name("Arasple")
            name("Score2")
            name("lilingfeng")
            name("Dreeam-qwq")
            name("FxRayHughes")
            name("TheFloodDragon")
        }
        dependencies {
            name("PlaceholderAPI").optional(true)
            name("Zaphkiel").optional(true)
            name("Skulls").optional(true)
            name("Vault").optional(true)
            name("PlayerPoints").optional(true)
            name("HeadDatabase").optional(true)
            name("Oraxen").optional(true)
            name("SkinsRestorer").optional(true)
            name("ItemsAdder").optional(true)
            name("floodgate").optional(true)
            name("FastScript").optional(true)
            name("Triton").optional(true)
        }
    }

    relocate("trplugins.menu", group.toString().lowercase())

    classifier = null
    version = taboolibVersion
}

repositories {
    mavenCentral()
    maven("https://repo.tabooproject.org/repository/releases")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.oraxen.com/releases")
    maven("https://jitpack.io")
}

dependencies {
    taboo(project(":common"))
    taboo(project(":api:receptacle"))
    taboo(project(":api:action"))

    // Libraries
    compileOnly("org.apache.commons:commons-lang3:3.14.0")
    compileOnly("com.electronwill.night-config:core:3.6.7")

    // Server Core
    compileOnly("ink.ptms.core:v12002:12002-minimize:mapped")
    compileOnly("ink.ptms.core:v12002:12002-minimize:universal")
    compileOnly("ink.ptms.core:v11904:11904-minimize:mapped")
    compileOnly("ink.ptms.core:v11904:11904-minimize:universal")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")
    compileOnly("ink.ptms.core:v11701:11701-minimize:universal")
    compileOnly("ink.ptms.core:v11605:11605")

    // Hook Plugins
    compileOnly("me.clip:placeholderapi:2.11.5") { isTransitive = false }
    compileOnly("ink.ptms:Zaphkiel:2.0.14") { isTransitive = false }
    compileOnly("ca.tweetzy:skulls:3.10.0") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:8bad2c4") { isTransitive = false }
    compileOnly("org.black_ixx:playerpoints:3.2.6") { isTransitive = false }
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.1") { isTransitive = false }
    compileOnly("io.th0rgal:oraxen:1.166.0") { isTransitive = false }
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.0.5-SNAPSHOT") { isTransitive = false }
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1") { isTransitive = false }
    compileOnly("org.geysermc.floodgate:api:2.2.2-SNAPSHOT") { isTransitive = false }
    compileOnly("com.github.tritonmc.Triton:core:3.9.3") { isTransitive = false }
    compileOnly("com.github.tritonmc.Triton:api:3.9.3") { isTransitive = false }
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.12.2") { isTransitive = false }
    compileOnly("com.github.FrancoBM12:API-MagicCosmetics:2.2.5") { isTransitive = false }

    compileOnly(fileTree("libs"))
}