package instafel.patcher.core.patches

import instafel.patcher.core.utils.FileSearchResult
import instafel.patcher.core.utils.SearchUtils
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
    isSingle = true
)
class RemoveAds: InstafelPatch() {

    lateinit var removeAdsFile: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find source file")
        object: InstafelTask() {
            override fun execute() {
                when (val result = SearchUtils.getFileContainsAllCords(smaliUtils,
                    listOf(
                        listOf("SponsoredContentController.insertItem"),
                    ))) {
                    is FileSearchResult.Success -> {
                        removeAdsFile = result.file
                        success("Remove ads source class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
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