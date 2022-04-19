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
    implementation("io.ktor:ktor-client-encoding:$ktor_version")

    // Reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.13.+")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.+") // Needed to serialize Instant

    // Log4j
    // 2.17 should be good-to-go vulnerability-wise https://blog.gradle.org/log4j-vulnerability
    implementation("org.apache.logging.log4j:log4j-api:2.17.0")
    implementation("org.apache.logging.log4j:log4j-core:2.17.0")

    testImplementation(kotlin("test-junit5"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    // Use the built-in JUnit support of Gradle.
    useJUnitPlatform()
}