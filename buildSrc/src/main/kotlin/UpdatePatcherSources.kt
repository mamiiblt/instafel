package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

open class UpdatePatcherSources : DefaultTask() {

    @Input
    lateinit var patcherJar: File
    @Input
    lateinit var appApk: File
    @Input
    lateinit var mainOutputDir: String
    @Input
    lateinit var targetDir: String

    @TaskAction
    fun runAndCopy() {
        val outputDirFile = project.file(mainOutputDir)

        val tempDir = File(outputDirFile, "${appApk.name.replace(".apk", "")}_temp")

        project.exec {
            workingDir = outputDirFile
            commandLine = listOf("java", "-jar", patcherJar.name, "csrc", appApk.name)
        }

        if (!tempDir.exists() || !tempDir.isDirectory) {
            throw GradleException("${tempDir.path} folder couldn't found!")
        }

        val targetDirFile = project.file(targetDir)
        tempDir.walkTopDown().forEach { file ->
            if (file.isFile) {
                val relativePath = file.toRelativeString(tempDir)
                val targetFile = File(targetDirFile, relativePath)
                targetFile.parentFile.mkdirs()
                Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                println("Copied: $relativePath")
            }
        }

        println("All new app sources successfully copied successfully to $targetDirFile")
    }
}
