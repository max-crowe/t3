import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    application
}

group = "t3"
version = "1.0-SNAPSHOT"

application {
    mainClass = "t3.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", "5.8.0")
    testImplementation("io.kotest", "kotest-assertions-core", "5.8.0")
    testImplementation("io.mockk", "mockk", "1.13.10")
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        val dependencies = configurations.runtimeClasspath.get().map(::zipTree)
        from(dependencies)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

