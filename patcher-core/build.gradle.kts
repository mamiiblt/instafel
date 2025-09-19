import IFLProjectManager.getCommitHash
import IFLProjectManager.Config
import IFLProjectManager.BuildConfig
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

plugins {
    kotlin("jvm")
    java
    `java-library`
}

lateinit var patchesJsonFile: File

group = "instafel"
version = project.getCommitHash()

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

tasks.named<Jar>("jar") {
    archiveBaseName.set("ifl-patcher-core")
    archiveClassifier.set("")
    destinationDirectory.set(file("${rootProject.rootDir}/.output"))

    manifest {
        attributes(
            "Patcher-Core-Commit" to project.getCommitHash(),
            "Patcher-Core-Supported-Version" to Config.patcher.version,
            "Patcher-Core-Branch" to "main"
        )
    }
}

tasks.register("build-jar") {
    group = "instafel"
    description = "Builds JAR file"

    dependsOn("jar", "generatePatchesJSON")

    doLast {
        println("Core JAR successfully generated.")
    }
}

tasks.register("generatePatchesJSON") {
    mustRunAfter("jar")
    group = "instafel"
    description = "Generates a patches.json file contains patch details"

    doLast{
        val jarFile = tasks.jar.get().archiveFile.get().asFile
        patchesJsonFile = generatePatchesJSON(jarFile)

        val tempJar = File(jarFile.parentFile, "temp-${jarFile.name}")
        JarFile(jarFile).use { jar ->
            JarOutputStream(tempJar.outputStream()).use { jos ->
                jar.entries().toList().forEach { entry ->
                    jos.putNextEntry(JarEntry(entry.name))
                    jar.getInputStream(entry).use { it.copyTo(jos) }
                    jos.closeEntry()
                }

                val entry = JarEntry("patches.json")
                jos.putNextEntry(entry)
                patchesJsonFile.inputStream().use { it.copyTo(jos) }
                jos.closeEntry()
            }
        }

        jarFile.delete()
        tempJar.renameTo(jarFile)
    }
}

tasks.register("release") {
    dependsOn("build-jar")

    group = "instafel"
    description = "Releases new core release"

    doLast {
        registerGithubReleaseTask(
            token = getInstafelEnvProperty("GH_TOKEN"),
            owner = "instafel",
            repo = "pc-rel",
            tagName = "${project.getCommitHash()}-${Config.patcher.version}",
            name =  "Core ${project.getCommitHash()}",
            assets = listOf(
                tasks.jar.get().archiveFile.get().asFile,
                patchesJsonFile,
                generatePatcherCoreBuildJSON(
                    commit = project.getCommitHash(),
                    branch = "main",
                    supportedVer = Config.patcher.version
                )
            ),
            body =  """
                # Build Information
        
                | Property | Value |
                | ------------- | ------------- |
                | Commit  | [${project.getCommitHash()}](https://github.com/mamiiblt/instafel/commit/${project.getCommitHash()}) |
                | Branch  | [main](https://github.com/mamiiblt/instafel) |
                | S. Version  | v${Config.patcher.version} |
                
                > Warning: This core package only supports Instafel Patcher v${Config.patcher.version} so you can't use that core package in newer or older patcher releases.

                ## More Information?

                For more information about the patcher, please visit [Instafel Wiki](https://instafel.app/wiki) or [Source Code](https://github.com/mamiiblt/instafel)
            """.trimIndent()
        )
    }
}