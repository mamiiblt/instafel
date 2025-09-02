val libs = rootProject.extra["patcherLibs"] as Map<*, *>
var config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config["patcher"] as Map<*, *>
val cliVersion = projectConfig["cli_version"] as String
val projectTag = projectConfig["tag"] as String
val commitHash: String by rootProject.extra

plugins {
    kotlin("jvm") version "2.2.10"
    java
    application
    id("com.gradleup.shadow") version "9.0.1"
    id("maven-publish")
}

group = "patcher"
version = "v$cliVersion-$commitHash-$projectTag"

apply(from = "publish.gradle.kts")

repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
}

sourceSets {
    main {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs["kotlin-reflect"]!!)
    implementation(libs["org-json"]!!)
    implementation(libs["commons-io"]!!)
    implementation(libs["okhttp"]!!)
    implementation(libs["apktool-lib"]!!)
    implementation(libs["classgraph"]!!)
    implementation(libs["jackson-databind"]!!)
    implementation(libs["jackson-yaml"]!!)
}

application {
    mainClass = "instafel.patcher.cli.MainKt"
}

tasks.shadowJar {
    archiveBaseName = "ifl-cli"
    archiveClassifier = ""
    destinationDirectory.set(file("${rootProject.rootDir}/patcher/output"))
    manifest {
        attributes(
            "Patcher-Cli-Version" to cliVersion,
            "Patcher-Cli-Commit" to commitHash,
            "Patcher-Cli-Branch" to "main",
            "Patcher-Cli-Tag" to projectTag
        )
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.register("build-jar") {
    dependsOn("shadowJar")

    doLast {
        println("All build tasks completed successfully")
    }
}