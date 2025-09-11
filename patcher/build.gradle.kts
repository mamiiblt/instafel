var config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config["patcher"] as Map<*, *>
val patcherVersion = projectConfig["patcher_version"] as String
val projectTag = projectConfig["tag"] as String
val commitHash: String by rootProject.extra

plugins {
    kotlin("jvm")
    java
    application
    id("com.gradleup.shadow")
}

group = "instafel"
version = "v$patcherVersion-$projectTag"

dependencies {
    implementation(IFLProjectManager.Deps.kotlin_stdlib)
    implementation(IFLProjectManager.Deps.kotlin_reflect)
    implementation(IFLProjectManager.Deps.org_json)
    implementation(IFLProjectManager.Deps.commons_io)
    implementation(IFLProjectManager.Deps.okhttp)
    implementation(IFLProjectManager.Deps.apktool_lib)
    implementation(IFLProjectManager.Deps.classgraph)
    implementation(IFLProjectManager.Deps.jackson_databind)
    implementation(IFLProjectManager.Deps.jackson_yaml)
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

tasks.register("build-jar") {
    group = "instafel"
    description = "Builds JAR file"

    dependsOn("shadowJar")

    doLast {
        println("All build tasks completed successfully")
    }
}

tasks.register("release") {
    dependsOn("build-jar")

    group = "instafel"
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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}