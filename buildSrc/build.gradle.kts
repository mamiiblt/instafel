plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.2.10"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.22")
    implementation("org.json:json:20240303")
}