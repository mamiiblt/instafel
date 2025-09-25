plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

// I need to use reflect 2.0.21 because Gradle 14.0 doesn't support 2.0.21x
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.20")
    implementation(buildSrcLibs.org.json)
    implementation(buildSrcLibs.gson)
}
