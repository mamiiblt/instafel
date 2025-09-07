import java.io.ByteArrayOutputStream
import java.io.File
import groovy.json.JsonSlurper

rootProject.name = "Instafel"

fun getGitCommitHash(): String {
    val output = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = output
    }
    return output.toString().trim()
}

val configFile = File(rootDir, "config/ifl_config.json")
val fallbackConfigFile = File(rootDir, "config/example.ifl_config.json")

val jsonData: Map<*, *> = if (configFile.exists()) {
    JsonSlurper().parse(configFile) as Map<*, *>
} else {
    println("Warning: ifl_config.json not found, using example.ifl_config.json instead.")
    JsonSlurper().parse(fallbackConfigFile) as Map<*, *>
}

println("Loaded & exported Instafel project configuration file")

gradle.rootProject {
    extra["commitHash"] = getGitCommitHash()
    extra["instafelConfig"] = jsonData
    extra["patcherLibs"] = mapOf(
        "kotlin-reflect" to "org.jetbrains.kotlin:kotlin-reflect:2.2.10",
        "org-json" to "org.json:json:20240303",
        "commons-io" to "commons-io:commons-io:2.20.0",
        "okhttp" to "com.squareup.okhttp3:okhttp:5.1.0",
        "apktool-lib" to "org.apktool:apktool-lib:2.12.0",
        "classgraph" to "io.github.classgraph:classgraph:4.8.181",
        "jackson-databind" to "com.fasterxml.jackson.core:jackson-databind:2.19.2",
        "jackson-yaml" to "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.2"
    )
}

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}


include(":app")
include(":patcher")
include(":patcher-core")
include(":updater")
include(":website")
include(":gplayapi")