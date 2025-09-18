import IFLProjectManager.getCommitHash
import IFLProjectManager.Config
import IFLProjectManager.BuildConfig

plugins {
    alias(libs.plugins.android.application)
}

group = "instafel"

dependencies {
    implementation(BuildConfig.android.appcompat)
    implementation(BuildConfig.android.material)
    implementation(BuildConfig.android.activity)
    implementation(BuildConfig.android.constraintlayout)
    implementation(BuildConfig.android.navigation_fragment)
    implementation(BuildConfig.android.navigation_ui)
    implementation(BuildConfig.android.preference)
    implementation(BuildConfig.shizuku_api)
    implementation(BuildConfig.shizuku_provider)
    implementation(BuildConfig.okhttp)
    implementation(BuildConfig.m3_preferences)
    implementation(BuildConfig.rootbeer)
    implementation(BuildConfig.android.work_manager)
}

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
        versionCode = Config.updater.versionCode
        versionName = "v${Config.updater.version} (${project.getCommitHash()})"
        buildConfigField("String", "IFLU_VERSION", "\"${Config.updater.version}\"")
        buildConfigField("String", "COMMIT", "\"${project.getCommitHash()}\"")
        buildConfigField("String", "BRANCH", "\"main\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs{
        create("release") {
            storeFile = File(rootDir, Config.updater.signing.ksPath)
            storePassword = Config.updater.signing.ksKeyPass
            keyAlias = Config.updater.signing.ksAlias
            keyPassword = Config.updater.signing.ksKeyPass
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
        val outputName = "ifl-updater-v${Config.updater.version}-debug.apk"
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
        val outputName = "ifl-updater-v${Config.updater.version}-release.apk"
        file("${project.projectDir}/build/outputs/apk/release/updater-release.apk")
            .copyTo(file("${rootProject.rootDir}/.output/$outputName"), overwrite = true)
        println("APK successfully copied into .output: $outputName")

        delete("${project.projectDir}/build")
        println("Build caches cleared.")
        println("All tasks completed succesfully")
    }
}

tasks.register("release") {
    group = "instafel"
    description = "Releases new updater release"
    dependsOn("generate-app-release")

    doLast {
        registerGithubReleaseTask(
            token = getInstafelEnvProperty("GH_TOKEN"),
            owner = "instafel",
            repo = "u-rel",
            tagName = "v${Config.updater.version}",
            name =  "Release v${Config.updater.version}",
            assets = listOf(
                File("${rootProject.rootDir}/.output/ifl-updater-v${Config.updater.version}-release.apk"),
                generateUpdaterBuildJSON(
                    version = Config.updater.version,
                    commit = project.getCommitHash(),
                    branch = "main",
                    channel = "release"
                )
            ),
            body =  """
                # Build Information
        
                | Property | Value |
                | ------------- | ------------- |
                | Version | v${Config.updater.version} |
                | Channel | release |
                | Base Commit | [${project.getCommitHash()}](https://github.com/mamiiblt/instafel/commit/${project.getCommitHash()}) |
                | Branch | [main](https://github.com/mamiiblt/instafel) |

                ## More Information?

                For more information about the updater, please visit [About Updater](https://instafel.app/about_updater) or [Source Code](https://github.com/mamiiblt/instafel)
            """.trimIndent()
        )
    }
}