tasks.register("mergeTranslations") {
    group = "ifl-general"
    description = "Loads latest translations from Crowdin into sources"

    doLast {

        println("Building and downloading translations...")

        val mergeUtils = CrowdinMergeUtils(
            crowdinApiToken = getInstafelEnvProperty("CROWDIN_TOKEN"),
            outputDir = File("${rootProject.rootDir}/.output/translations"),
            rootDir = rootProject.rootDir
        )

        /*mergeUtils.startBuild()
        mergeUtils.waitUntilBuildFinish()*/
        mergeUtils.downloadBuild("48")
        mergeUtils.unzipBuildFile()

        mergeUtils.mergeWebsiteSources()
        mergeUtils.mergeUpdaterSources()
        mergeUtils.mergeAppSources()
    }
}
