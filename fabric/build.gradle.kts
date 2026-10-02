plugins {
    `java-library`
    id("fabric-loom")
}

version = "${property("minecraft_version")}-${property("mod_version")}" +
    (System.getenv("BUILD_NUMBER")?.let { ".$it" } ?: "")

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}
dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    implementation(project(":common"))
}
loom {
    accessWidenerPath.set(file("src/main/resources/lodestone.accesswidener"))
    mixin.useLegacyMixinAp.set(false)
    runs {
        register("datagen") {
            client()
            name("Lodestone Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.modid=lodestone")
            vmArg("-Dfabric-api.datagen.output-dir=${rootProject.file("common/src/generated/resources").absolutePath}")
            runDir("build/datagen")
        }
    }
    mods {
        register("lodestone") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":common").extensions.getByType<SourceSetContainer>()["main"])
        }
    }
}
tasks.processResources {
    val modVersion = "${rootProject.property("minecraft_version")}-${rootProject.property("mod_version")}" +
        (System.getenv("BUILD_NUMBER")?.let { ".$it" } ?: "")
    inputs.property("version", modVersion)
    filesMatching("fabric.mod.json") {
        expand("version" to modVersion)
    }
}
tasks.named<Jar>("jar") {
    from(project(":common").extensions.getByType<SourceSetContainer>()["main"].output)
}
tasks.named<Jar>("sourcesJar") {
    from(project(":common").extensions.getByType<SourceSetContainer>()["main"].allSource)
}
