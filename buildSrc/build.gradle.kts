plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.2.20"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
val configPath = "${rootDir.parentFile}/config/build-config.json"
val jsonText = File(configPath).readText()
val orgJsonVersion = Regex("\"org_json\"\\s*:\\s*\\{[^}]*\"version\"\\s*:\\s*\"([^\"]+)\"").find(jsonText)?.groupValues?.get(1)
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${"2.2.20"}")
    implementation("org.json:json:$orgJsonVersion")
}