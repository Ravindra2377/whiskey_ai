import org.gradle.api.tasks.Delete
import org.gradle.jvm.tasks.Jar

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

group = "com.boozer.nexus"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.boozer.nexus.desktop.NexusDesktopLauncher")
}

javafx {
    version = "17.0.8"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.fxml")
}

dependencies {
    // JavaFX dependencies are supplied by the plugin
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    archiveBaseName.set("nexus-desktop")
    manifest {
        attributes["Main-Class"] = "com.boozer.nexus.desktop.NexusDesktopLauncher"
    }
}

val targetDir = layout.projectDirectory.dir("target")

tasks.register<Copy>("copyJarToTarget") {
    dependsOn(tasks.named("jar"))
    from(tasks.named<Jar>("jar"))
    into(targetDir)
}

tasks.named("build") {
    dependsOn("copyJarToTarget")
}

tasks.register<Delete>("cleanTarget") {
    delete(targetDir)
}

tasks.named("clean") {
    dependsOn("cleanTarget")
}
