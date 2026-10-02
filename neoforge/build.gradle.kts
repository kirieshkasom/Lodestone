plugins {
    id("java-library")
    idea
    id("maven-publish")
    id("net.neoforged.moddev")
}

version = "${property("minecraft_version")}-${property("mod_version")}"
if (System.getenv("BUILD_NUMBER") != null) {
    version = "$version.${System.getenv("BUILD_NUMBER")}"
}
val baseArchivesName = project.property("mod_id").toString()
base {
    archivesName.set("${project.property("mod_id")}-neoforge")
}
group = "${property("mod_group_id")}"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


tasks.withType<Javadoc>().configureEach {
    isFailOnError = false
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

val localRuntime: Configuration by configurations.creating
configurations.runtimeClasspath {
    extendsFrom(localRuntime)
}

neoForge {
    version = project.property("neo_version").toString()

    parchment {
        mappingsVersion.set(project.property("parchment_mappings_version").toString())
        minecraftVersion.set(project.property("parchment_minecraft_version").toString())
    }
    accessTransformers {
        publish(file("src/main/resources/META-INF/blockproperties.cfg"))
        publish(file("src/main/resources/META-INF/miscellaneous.cfg"))
        publish(file("src/main/resources/META-INF/recipebuilders.cfg"))
        publish(file("src/main/resources/META-INF/rendering.cfg"))
        publish(file("src/main/resources/META-INF/renderstates.cfg"))
    }
    setAccessTransformers(
        "src/main/resources/META-INF/blockproperties.cfg",
        "src/main/resources/META-INF/miscellaneous.cfg",
        "src/main/resources/META-INF/recipebuilders.cfg",
        "src/main/resources/META-INF/rendering.cfg",
        "src/main/resources/META-INF/renderstates.cfg"
    )
    runs {
        register("client") {
            client()

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id").toString())
        }

        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id").toString())
        }

        register("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id").toString())
        }

        register("data") {
            data()
            programArguments.addAll(
                "--mod", project.property("mod_id").toString(),
                "--all",
                "--output", rootProject.file("common/src/generated/resources/").absolutePath,
                "--existing", rootProject.file("common/src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create("${property("mod_id")}") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":common").extensions.getByType<SourceSetContainer>()["main"])
        }
    }
}

repositories {
    flatDir {
        dirs("lib")
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
    implementation(project(":common"))
    compileOnlyApi("top.theillusivec4.curios:curios-neoforge:${property("curios_version")}:api")
    localRuntime("top.theillusivec4.curios:curios-neoforge:${property("curios_version")}")

//    implementation("curse.maven:architectury-api-419699:5786327")
//    implementation("curse.maven:octo-lib-916747:6932487")
//    implementation("curse.maven:immersive-ui-1021685:6886575")

//    runtimeOnly(("com.sammy.malum:malum:${property("minecraft_version")}-1.9.0.255"))

    compileOnly("maven.modrinth:sodium:mc${property("minecraft_version")}-${property("sodium_version")}-neoforge")
    compileOnly("maven.modrinth:iris:${property("iris_version")}+${property("minecraft_version")}-neoforge")
    //runtimeOnly("maven.modrinth:sodium:mc${property("minecraft_version")}-${property("sodium_version")}-neoforge")
    //runtimeOnly("maven.modrinth:iris:${property("iris_version")}+${property("minecraft_version")}-neoforge")



    compileOnly("io.github.spair:imgui-java-app:${property("imgui_version")}")
    compileOnly("io.github.spair:imgui-java-lwjgl3:${property("imgui_version")}")
    compileOnly("io.github.spair:imgui-java-binding:${property("imgui_version")}")
    compileOnly("io.github.spair:imgui-java-natives-windows:${property("imgui_version")}")
    compileOnly("io.github.spair:imgui-java-natives-macos-ft:${property("imgui_version")}")
    compileOnly("io.github.spair:imgui-java-natives-linux:${property("imgui_version")}")
    localRuntime("io.github.spair:imgui-java-app:${property("imgui_version")}")
    localRuntime("io.github.spair:imgui-java-lwjgl3:${property("imgui_version")}")
    localRuntime("io.github.spair:imgui-java-binding:${property("imgui_version")}")
    localRuntime("io.github.spair:imgui-java-natives-windows:${property("imgui_version")}")
    localRuntime("io.github.spair:imgui-java-natives-macos-ft:${property("imgui_version")}")
    localRuntime("io.github.spair:imgui-java-natives-linux:${property("imgui_version")}")
    add("additionalRuntimeClasspath", "io.github.spair:imgui-java-app:${property("imgui_version")}")
    add("additionalRuntimeClasspath", "io.github.spair:imgui-java-lwjgl3:${property("imgui_version")}")
    add("additionalRuntimeClasspath", "io.github.spair:imgui-java-binding:${property("imgui_version")}")
    add("additionalRuntimeClasspath", "io.github.spair:imgui-java-natives-windows:${property("imgui_version")}")
    add("additionalRuntimeClasspath", "io.github.spair:imgui-java-natives-macos-ft:${property("imgui_version")}")
    add("additionalRuntimeClasspath", "io.github.spair:imgui-java-natives-linux:${property("imgui_version")}")
}
val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "minecraft_version" to project.findProperty("minecraft_version") as String,
        "minecraft_version_range" to project.findProperty("minecraft_version_range") as String,
        "neo_version" to project.findProperty("neo_version") as String,
        "neo_version_range" to project.findProperty("neo_version_range") as String,
        "loader_version_range" to project.findProperty("loader_version_range") as String,
        "mod_id" to project.findProperty("mod_id") as String,
        "mod_name" to project.findProperty("mod_name") as String,
        "mod_license" to project.findProperty("mod_license") as String,
        "mod_version" to project.findProperty("mod_version") as String,
        "mod_authors" to project.findProperty("mod_authors") as String,
        "mod_description" to project.findProperty("mod_description") as String
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)

    // Exclude .java files or any other files that shouldn't have template expansion
    filesMatching("**/*.java") {
        exclude()
    }

    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}
// Include the output of "generateModMetadata" as an input directory for the build.
// This works with both building through Gradle and the IDE.
sourceSets["main"].resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)


java {
    withJavadocJar()
    withSourcesJar()
}
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = "${property("mod_id")}"
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file://${System.getenv("local_maven")}")
        }
    }
}

idea {
    module {
        for (fileName in listOf("run", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.named<Jar>("jar") {
    from(project(":common").extensions.getByType<SourceSetContainer>()["main"].output)
}
tasks.named<Jar>("sourcesJar") {
    from(project(":common").extensions.getByType<SourceSetContainer>()["main"].allSource)
}
