import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "t3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", "5.8.0")
    testImplementation("io.kotest", "kotest-assertions-core", "5.8.0")
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
}

