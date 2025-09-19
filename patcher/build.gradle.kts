import IFLProjectManager.Config
import IFLProjectManager.BuildConfig
import IFLProjectManager.getCommitHash

plugins {
    kotlin("jvm")
    java
    application
    id("com.gradleup.shadow")
}

group = "instafel"
version = "v${Config.patcher.version}-${Config.patcher.tag}"

dependencies {
    implementation(BuildConfig.kotlin_stdlib)
    implementation(BuildConfig.kotlin_reflect)
    implementation(BuildConfig.kotlin_coroutines)
    implementation(BuildConfig.org_json)
    implementation(BuildConfig.commons_io)
    implementation(BuildConfig.okhttp)
    implementation(BuildConfig.apktool_lib)
    implementation(BuildConfig.classgraph)
    implementation(BuildConfig.jackson_databind)
    implementation(BuildConfig.jackson_yaml)
    implementation(BuildConfig.gson)
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