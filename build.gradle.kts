plugins {
    base
    id("net.neoforged.moddev") version "2.0.143" apply false
    id("fabric-loom") version "1.8.13" apply false
}

subprojects.forEach { moduleProject ->
    moduleProject.group = rootProject.property("mod_group_id").toString()
    moduleProject.version = "${rootProject.property("minecraft_version")}-${rootProject.property("mod_version")}" +
        (System.getenv("BUILD_NUMBER")?.let { ".$it" } ?: "")
    val moduleName = moduleProject.name
    moduleProject.plugins.withId("java-library") {
        moduleProject.extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(21))
            withSourcesJar()
            withJavadocJar()
        }
        moduleProject.extensions.configure<BasePluginExtension> {
            archivesName.set("${rootProject.property("mod_id")}-${moduleName}")
        }
        moduleProject.tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.compilerArgs.addAll(listOf("-Xmaxerrs", "1000"))
        }
        moduleProject.tasks.withType<Javadoc>().configureEach {
            isFailOnError = false
            (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }
}

tasks.named("build") {
    dependsOn(":common:build", ":fabric:build", ":neoforge:build")
}
tasks.named("clean") {
    dependsOn(":common:clean", ":fabric:clean", ":neoforge:clean")
}
