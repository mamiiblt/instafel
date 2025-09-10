val libs = rootProject.extra["patcherLibs"] as Map<*, *>
var config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config["patcher"] as Map<*, *>
val patcherVersion = projectConfig["patcher_version"] as String
val projectTag = projectConfig["tag"] as String
val commitHash: String by rootProject.extra

plugins {
    kotlin("jvm")
    java
    application
    id("com.gradleup.shadow") version "9.0.1"
}

group = "instafel"
version = "v$patcherVersion"

repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
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
    mainClass = "instafel.patcher.MainKt"
}

tasks.shadowJar {
    archiveBaseName = "ifl-patcher"
    archiveClassifier = ""
    destinationDirectory.set(file("${rootProject.rootDir}/.output"))

    manifest {
        attributes(
            "Patcher-Cli-Version" to patcherVersion,
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
    group = "build"
    description = "Builds JAR file"

    dependsOn("shadowJar")

    doLast {
        println("All build tasks completed successfully")
    }
}

tasks.register("release") {
    group = "publishing"
    description = "Releases new patcher release"

    doLast {
        registerGithubReleaseTask(
            token = getInstafelEnvProperty("GH_TOKEN"),
            owner = "instafel",
            repo = "p-rel",
            tagName = "v$patcherVersion",
            name =  "Release v$patcherVersion",
            assets = listOf(
                tasks.shadowJar.get().archiveFile.get().asFile,
                generatePatcherBuildJSON(
                    version = patcherVersion,
                    commit = commitHash,
                    channel = projectTag
                )
            ),
            body =  """
                # Build Information
        
                | Property | Value |
                | ------------- | ------------- |
                | Version  | v$patcherVersion |
                | Channel  | $projectTag |
                | Base Commit  | [$commitHash](https://github.com/mamiiblt/instafel/commit/$commitHash) |
                | Branch  | [main](https://github.com/mamiiblt/instafel) |

                ## More Information?

                For more information about the patcher, please visit [Instafel Wiki](instafel.app/wiki) or [Source Code](https://github.com/mamiiblt/instafel)
            """.trimIndent()
        )
    }
}
