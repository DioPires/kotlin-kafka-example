import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")

}

repositories {
    mavenCentral()
    jcenter()
}

group = "com.piresdio.kafkaexample"

dependencies {
    implementation(project(":database"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9+")
    implementation("org.apache.kafka:kafka-clients:2.0.0")
}

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

task("runConsumers", JavaExec::class) {
    main = "com.piresdio.kafkaexample.MainKt"
    classpath = sourceSets["main"].runtimeClasspath
}