tasks.register("mergeTranslations") {
    group = "ifl-general"
    description = "Loads latest translations from Crowdin into sources"

    doLast {

        println("Building and downloading translations...")

        val mergeUtils = CrowdinMergeUtils(
            crowdinApiToken = getInstafelEnvProperty("CROWDIN_TOKEN"),
            outputDir = File("${rootProject.rootDir}/.output/translations")
        )

        mergeUtils.startBuild()
        mergeUtils.waitUntilBuildFinish()
        mergeUtils.downloadBuild()
        mergeUtils.unzipBuildFile()
    }
}
