import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.boozer"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.named("annotationProcessor").get())
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.apache.httpcomponents:httpmime:4.5.14")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    enabled = false
}

tasks.named<JavaCompile>("compileJava") {
    options.encoding = "UTF-8"
    include("com/boozer/nexus/cli/**")
    include("com/boozer/nexus/persistence/**")
    include("com/boozer/nexus/voice/**")
    include("com/boozer/nexus/codegen/**")
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<BootJar>("bootJar") {
    mainClass.set("com.boozer.nexus.cli.NexusCliApplication")
    archiveFileName.set("nexus-1.0.0.jar")
    destinationDirectory.set(layout.projectDirectory.dir("target"))
}

tasks.named<Delete>("clean") {
    delete("target")
}
