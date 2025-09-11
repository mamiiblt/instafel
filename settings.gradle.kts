import java.io.ByteArrayOutputStream
import java.io.File
import groovy.json.JsonSlurper

rootProject.name = "Instafel"
System.setProperty("project.rootDir", rootDir.absolutePath)

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
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
    }
}

pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.20"
        id("com.gradleup.shadow") version "9.1.0"
    }
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