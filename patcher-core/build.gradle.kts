import IFLProjectManager.getCommitHash
import IFLProjectManager.Config
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

plugins {
    java
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

lateinit var patchesJsonFile: File

group = "instafel"
version = project.getCommitHash()

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