plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-chrome-driver
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.8.1")
    // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-support
    implementation("org.seleniumhq.selenium:selenium-support:4.8.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}