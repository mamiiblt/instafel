import IFLProjectManager.Config
import IFLProjectManager.getCommitHash

plugins {
    alias(libs.plugins.android.application)
}

group = "instafel.app"

android {
    namespace = "instafel.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "instafel.app"
        minSdk = 28
        targetSdk = 36
        versionCode = 1 // it doesn't matter
        versionName = project.getCommitHash()// it doesn't matter
    }

    signingConfigs{
        create("release") {
            storeFile = File(rootDir, Config.app.signing.ksPath)
            storePassword = Config.app.signing.ksKeyPass
            keyAlias = Config.app.signing.ksAlias
            keyPassword = Config.app.signing.ksPass
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = false
            isReturnDefaultValues = true
        }
    }
}

tasks.register("generate-app-debug") {
    group = "instafel"
    description = "Builds APK as debug"
    dependsOn("assembleDebug")

    doLast {
        val outputName = "ifl-app-${project.getCommitHash()}-debug.apk"
        file("${project.projectDir}/build/outputs/apk/debug/app-debug.apk")
            .copyTo(file("${project.rootDir}/.output/$outputName"), overwrite = true)
        println("APK successfully copied: $outputName")

        delete("${project.projectDir}/build")
        println("Build caches cleared.")
        println("All tasks completed succesfully")
    }
}

tasks.register("generate-app-release") {
    group = "instafel"
    description = "Builds APK as release"
    dependsOn("assembleRelease")

    doLast {
        val outputName = "ifl-app-${project.getCommitHash()}-release.apk"
        file("${project.projectDir}/build/outputs/apk/release/app-release.apk")
            .copyTo(file("${project.rootDir}/.output/$outputName"), overwrite = true)
        println("APK successfully copied: $outputName")

        delete("${project.projectDir}/build")
        println("Build caches cleared.")
        println("All tasks completed succesfully")
    }
}

dependencies {
    implementation(libs.android.appcompat)
    implementation(libs.android.material)
    implementation(libs.android.activity)
    implementation(libs.android.constraintlayout)
}