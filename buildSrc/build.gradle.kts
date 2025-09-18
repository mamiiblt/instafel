plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.2.20"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
val configPath = "${rootDir.parentFile}/config/ifl_config.json"
val jsonText = File(configPath).readText()
val orgJsonVersion = Regex("\"org_json\"\\s*:\\s*\\{[^}]*\"ver\"\\s*:\\s*\"([^\"]+)\"").find(jsonText)?.groupValues?.get(1)
val kotlinRlVersion = Regex("\"kotlin_reflect\"\\s*:\\s*\\{[^}]*\"ver\"\\s*:\\s*\"([^\"]+)\"").find(jsonText)?.groupValues?.get(1)
val kotlinStdlib = Regex("\"kotlin_stdlib\"\\s*:\\s*\\{[^}]*\"ver\"\\s*:\\s*\"([^\"]+)\"").find(jsonText)?.groupValues?.get(1)
val gsonVersion = Regex("\"gson\"\\s*:\\s*\\{[^}]*\"ver\"\\s*:\\s*\"([^\"]+)\"").find(jsonText)?.groupValues?.get(1)

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinStdlib")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${"2.2.20"}")
    implementation("org.json:json:$orgJsonVersion")
    implementation("com.google.code.gson:gson:${gsonVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinRlVersion")
}