import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project

plugins {
    kotlin("jvm") version "1.6.0"
}
group = "me.alexray"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    // Ktor
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    // Reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.+")

    testImplementation(kotlin("test-junit5"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}