plugins {
    kotlin("jvm") version "2.4.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}