import java.io.ByteArrayOutputStream

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