import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

plugins {
    kotlin("jvm")
    java
    `java-library`
}

val config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config["patcher"] as Map<*, *>
val coreSupportedVersion = projectConfig["core_supported_version"] as String
val commitHash: String by rootProject.extra
lateinit var patchesJsonFile: File

group = "instafel"
version = commitHash

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

tasks.named<Jar>("jar") {
    archiveBaseName.set("ifl-patcher-core")
    archiveClassifier.set("")
    destinationDirectory.set(file("${rootProject.rootDir}/.output"))

    manifest {
        attributes(
            "Patcher-Core-Commit" to commitHash,
            "Patcher-Core-Supported-Version" to coreSupportedVersion,
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
        val patchesJsonFile = generatePatchesJSON(jarFile)

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
            tagName = "$commitHash-$coreSupportedVersion",
            name =  "Core $commitHash",
            assets = listOf(
                tasks.jar.get().archiveFile.get().asFile,
                generatePatcherCoreBuildJSON(
                    commit = commitHash,
                    branch = "main",
                    supportedVer = coreSupportedVersion
                )
            ),
            body =  """
                # Build Information
        
                | Property | Value |
                | ------------- | ------------- |
                | Commit  | [$commitHash](https://github.com/mamiiblt/instafel/commit/$commitHash) |
                | Branch  | [main](https://github.com/mamiiblt/instafel) |
                | S. Version  | v$coreSupportedVersion |
                
                > Warning: This core package only supports Instafel Patcher v$coreSupportedVersion so you can't use that core package in newer or older patcher releases.

                ## More Information?

                For more information about the patcher, please visit [Instafel Wiki](https://instafel.app/wiki) or [Source Code](https://github.com/mamiiblt/instafel)
            """.trimIndent()
        )
    }
}