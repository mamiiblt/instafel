package instafel.patcher.core.patches

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File

// Thanks to ReVanced developers for made this patch possible!
@PInfos.PatchInfo(
    name = "Remove Ads",
    shortname = "remove_ads",
    desc = "Remove Ads in Instagram",
    author = "mamiiblt",
    isSingle = true
)
class RemoveAds: InstafelPatch() {

    lateinit var removeAdsFile: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find source file")
        object: InstafelTask() {
            override fun execute() {
                var scannedFileSize = 0

                run loop@{
                    smaliUtils.smaliFolders.forEach { folder ->
                        val xFolder = File(Utils.mergePaths(folder.absolutePath, "X"))
                        Log.info("Searching in X folder of ${folder.name}")

                        if (!xFolder.exists()) return@forEach

                        FileUtils.iterateFiles(xFolder, null, true).forEach { file ->
                            scannedFileSize++

                            val fContent = smaliUtils.getSmaliFileContent(file.absolutePath)
                            val matchLines = smaliUtils.getContainLines(fContent, "SponsoredContentController.insertItem")

                            if (matchLines.size == 1) {
                                removeAdsFile = file
                                Log.info("File found in ${file.name} at ${folder.name}")
                                return@loop
                            }
                        }
                    }
                }

                if (::removeAdsFile.isInitialized) {
                    Log.info("Totally scanned $scannedFileSize files in X folders")
                    success("Remove ads controller file found.")
                } else {
                    failure("Remove ads controller file cannot be found")
                }
            }
        },
        @PInfos.TaskInfo("Change method return")
        object: InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(removeAdsFile.absolutePath).toMutableList()
                var methodLine = -1

                fContent.forEachIndexed { index, line ->
                    if (line.contains("SponsoredContentController.insertItem")) {
                        for (i in index downTo 0) {
                            if (fContent[i].contains(".method")) {
                                methodLine = i
                                break
                            }
                        }
                        if (methodLine != -1) return@forEachIndexed
                    }
                }

                if (methodLine != -1) {
                    val lines = listOf(
                        "",
                        "    const/4 v0, 0x1",
                        "",
                        "    return v0"
                    )

                    fContent.add(methodLine + 2, lines.joinToString("\n"))
                    FileUtils.writeLines(removeAdsFile, fContent)
                    success("Method return successfully applied")
                } else {
                    failure("Required method cannot be found.")
                }
            }
        }
    )
}