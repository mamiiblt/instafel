plugins {
    alias(libs.plugins.android.application)
}

var config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config[project.name] as Map<*, *>
val androidConfig = projectConfig["androidConfig"] as Map<*, *>
val keystoreConfig = androidConfig["keystore"] as Map<*, *>
val depsConfig = projectConfig["dependencyConfig"] as Map<*, *>

val projectVersion = projectConfig["version"] as String

val commitHash: String by rootProject.extra

group = "instafel"

apply(from = "publish.gradle.kts")

android {
    namespace = "me.mamiiblt.instafel.updater"
    compileSdk = 36

    // disable include metadata in dep infos
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    defaultConfig {
        applicationId = "me.mamiiblt.instafel.updater"
        minSdk = 26
        targetSdk = 36
        versionCode = androidConfig["versionCode"] as Int
        versionName = "v$projectVersion ($commitHash)"
        buildConfigField("String", "IFLU_VERSION", "\"$projectVersion\"")
        buildConfigField("String", "COMMIT", "\"$commitHash\"")
        buildConfigField("String", "BRANCH", "\"main\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs{
        create("release") {
            storeFile = File(rootDir, keystoreConfig["ksPath"] as String)
            storePassword = keystoreConfig["ksKeyPass"] as String
            keyAlias = keystoreConfig["ksAlias"] as String
            keyPassword = keystoreConfig["ksPass"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures {
        aidl = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.register("generate-app-debug") {
    group = "instafel"
    description = "Builds APK as debug"
    dependsOn("assembleDebug")

    doLast {
        val outputName = "ifl-updater-v$projectVersion-$commitHash-debug.apk"
        file("${project.projectDir}/build/outputs/apk/debug/updater-debug.apk")
            .copyTo(file("${rootProject.rootDir}/.output/$outputName"), overwrite = true)
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
        val outputName = "ifl-updater-v$projectVersion-$commitHash-release.apk"
        file("${project.projectDir}/build/outputs/apk/release/updater-release.apk")
            .copyTo(file("${rootProject.rootDir}/.output/$outputName"), overwrite = true)
        println("APK successfully copied: $outputName")

        delete("${project.projectDir}/build")
        println("Build caches cleared.")
        println("All tasks completed succesfully")
    }
}

dependencies {
    implementation(IFLProjectManager.Deps.Android.appcompat)
    implementation(IFLProjectManager.Deps.Android.material)
    implementation(IFLProjectManager.Deps.Android.activity)
    implementation(IFLProjectManager.Deps.Android.constraintlayout)
    implementation(IFLProjectManager.Deps.Android.navigation_fragment)
    implementation(IFLProjectManager.Deps.Android.navigation_ui)
    implementation(IFLProjectManager.Deps.Android.preference)
    implementation(IFLProjectManager.Deps.shizuku_api)
    implementation(IFLProjectManager.Deps.shizuku_provider)
    implementation(IFLProjectManager.Deps.okhttp)
    implementation(IFLProjectManager.Deps.m3_preferences)
    implementation(IFLProjectManager.Deps.rootbeer)
    implementation(IFLProjectManager.Deps.Android.work_manager)
}
