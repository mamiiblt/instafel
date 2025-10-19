import IFLProjectManager.Config
import IFLProjectManager.getCommitHash

plugins {
    java
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadowjar)
}

group = "instafel"
version = "v${Config.patcher.version}-${Config.patcher.tag}"

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.coroutines)
    implementation(libs.org.json)
    implementation(libs.apache.commons.io)
    implementation(libs.okhttp)
    implementation(libs.apktool.lib)
    implementation(libs.classgraph)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.yaml)
    implementation(libs.gson)
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
            "Patcher-Cli-Version" to Config.patcher.version,
            "Patcher-Cli-Commit" to project.getCommitHash(),
            "Patcher-Cli-Branch" to "main",
            "Patcher-Cli-Tag" to Config.patcher.tag
        )
    }
}

tasks.register("build-jar") {
    group = "ifl-patcher"
    description = "Builds JAR file"

    dependsOn("shadowJar")

    doLast {
        println("All build tasks completed successfully")
    }
}

tasks.register("release") {
    dependsOn("build-jar")

    group = "ifl-patcher"
    description = "Releases new patcher release"

    doLast {
        registerGithubReleaseTask(
            token = getInstafelEnvProperty("GH_TOKEN"),
            owner = "instafel",
            repo = "p-rel",
            tagName = "v${Config.patcher.version}",
            name =  "Release v${Config.patcher.version}",
            assets = listOf(
                tasks.shadowJar.get().archiveFile.get().asFile,
                generatePatcherBuildJSON(
                    version = Config.patcher.version,
                    channel = Config.patcher.tag,
                    commit = project.getCommitHash(),
                )
            ),
            body =  """
                # Build Information
        
                | Property | Value |
                | ------------- | ------------- |
                | Version  | v${Config.patcher.version} |
                | Channel  | ${Config.patcher.tag} |
                | Base Commit  | [${project.getCommitHash()}](https://github.com/mamiiblt/instafel/commit/${project.getCommitHash()}) |
                | Branch  | [main](https://github.com/mamiiblt/instafel) |

                ## More Information?

                For more information about the patcher, please visit [Instafel Wiki](https://instafel.app/wiki) or [Source Code](https://github.com/mamiiblt/instafel)
            """.trimIndent()
        )
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}