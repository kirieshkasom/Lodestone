plugins {
    `java-library`
    id("net.neoforged.moddev")
}

version = "${property("minecraft_version")}-${property("mod_version")}" +
    (System.getenv("BUILD_NUMBER")?.let { ".$it" } ?: "")


neoForge {
    neoFormVersion = property("neoform_version").toString()
    accessTransformers {
        from(fileTree("src/main/resources/META-INF") {
            include("*.cfg")
        })
    }
    parchment {
        mappingsVersion.set(property("parchment_mappings_version").toString())
        minecraftVersion.set(property("parchment_minecraft_version").toString())
    }
}
sourceSets.main {
    resources.srcDir("src/generated/resources")
    resources.exclude("META-INF/*.cfg", ".cache/**")
}
repositories {
    flatDir {
        dirs(rootProject.file("lib"))
    }
    mavenLocal()
    mavenCentral()
    maven { //Our Stuff
        name = "BlameJared maven"
        url = uri("https://maven.blamejared.com/")
    }
    maven { //Curios
        name = "Curios maven"
        url = uri("https://maven.theillusivec4.top/")
    }
    maven { //JEI
        name = "JEI maven"
        url = uri("https://dvs1.progwml6.com/files/maven")
    }

    maven { //Curse Maven, Generic
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    maven { //ParchmentMC Maven, Generic
        name = "ParchmentMC"
        url = uri("https://maven.parchmentmc.org")
        content {
            includeGroup("org.parchmentmc.data")
        }
    }
    maven { //Mod Maven, Generic
        name = "ModMaven"
        url = uri("https://modmaven.dev")
    }
    maven { //Modrinth Maven, Generic
        name = "Modrinth maven"
        url = uri("https://api.modrinth.com/maven")
    }

    maven { //KubeJS
        url = uri("https://maven.latvian.dev/releases")
        content {
            includeGroup("dev.latvian.mods")
            includeGroup("dev.latvian.apps")
        }
    }
    maven { //KubeJS Dependencies
        name = "jitpack"
        url = uri("https://jitpack.io")
        content {
            includeGroup("io.github")
            includeGroup("com.github.rtyley")
        }
    }
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
    compileOnly("io.github.spair:imgui-java-app:${property("imgui_version")}")
    compileOnly("io.github.spair:imgui-java-lwjgl3:${property("imgui_version")}")
    compileOnly("io.github.spair:imgui-java-binding:${property("imgui_version")}")
}
