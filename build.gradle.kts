tasks.register("mergeTranslations") {
    group = "ifl-general"
    description = "Loads latest translations from Crowdin into sources"

    doLast {
        val mergeUtils = CrowdinMergeUtils(
            crowdinApiToken = getInstafelEnvProperty("CROWDIN_TOKEN"),
            outputDir = File("${rootProject.rootDir}/.output/translations"),
            rootDir = rootProject.rootDir
        )

        mergeUtils.startBuild()
        mergeUtils.waitUntilBuildFinish()
        mergeUtils.downloadBuild()
        mergeUtils.unzipBuildFile()
        mergeUtils.mergeWebsiteSources()
        mergeUtils.mergeUpdaterSources()
        mergeUtils.mergeAppSources()
        mergeUtils.updateLocalesInPatcher()

        println("All translation merge steps ran successfully.")
    }
}

tasks.register("deleteOutputFolderIfExists"){
    doLast {
        val outputDir = File("${rootProject.rootDir}/.output")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        } else {
            delete(outputDir)
            outputDir.mkdirs()
        }
    }
}

tasks.register("updatePatcherSources") {
    dependsOn(
        "deleteOutputFolderIfExists",
        project(":app").tasks.named("generate-app-release"),
        project(":patcher").tasks.named("build-jar"),
        project(":patcher-core").tasks.named("build-jar")
    )

    group = "ifl-general"
    description = "Update patcher app sources with latest sources"

    doLast {
        val outputDir = File("${rootProject.rootDir}/.output")
        if (!outputDir.exists()) {
            println("Output directory does not exist!")
            return@doLast
        }

        val iflAppFile = outputDir.listFiles { file ->
            file.isFile && file.name.startsWith("ifl-app")
        }?.firstOrNull()

        val iflPatcherFile = outputDir.listFiles { file ->
            file.isFile && file.name.startsWith("ifl-patcher") && !file.name.startsWith("ifl-patcher-core")
        }?.firstOrNull()

        if (iflAppFile != null && iflPatcherFile != null) {
            val runJarTask = project.tasks.register<tasks.UpdatePatcherSources>("runJarAndCopyTemp") {
                patcherJar = iflPatcherFile
                appApk = iflAppFile
                mainOutputDir = "${rootProject.rootDir}/.output"
                targetDir = "${rootProject.rootDir}/patcher-core/src/main/resources/ifl_sources"
            }

            runJarTask.get().actions.forEach { it.execute(runJarTask.get()) }
        } else {
            println("Output files cannot loaded correctly.")
        }
    }
}