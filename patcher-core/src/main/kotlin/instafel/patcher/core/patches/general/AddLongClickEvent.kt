package instafel.patcher.core.patches.general

import instafel.patcher.core.utils.FileSearchResult
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Add Long Click Event",
    shortname = "add_long_click_event",
    desc = "This patch must be applied for Instafel Menu",
    isSingle = false
)
class AddLongClickEvent: InstafelPatch() {

    lateinit var longClickClass: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find long click smali file")
        object: InstafelTask() {
            override fun execute() {
                when (val result = SearchUtils.getFileContainsAllCords(smaliUtils,
                    listOf(
                        listOf("notifications_entry_point_impression"),
                        listOf("null cannot be cast to non-null type android.content.Context")
                    ))) {
                    is FileSearchResult.Success -> {
                        longClickClass = result.file
                        success("Long click trigger class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Change dev options caller to Instafel menu caller")
        object: InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(longClickClass.absolutePath).toMutableList()
                var lock = false

                for (i in fContent.indices) {
                    if (
                        fContent[i].contains("new-instance") &&
                        fContent[i + 2].contains("invoke-direct") &&
                        fContent[i + 7].contains("Landroid/content/Context")
                    ) {
                        val vNameRw = fContent[i].trim().split(" ")[1]
                        val veriablePart = vNameRw.substring(0, vNameRw.length - 1)

                        fContent[i] = "    new-instance $veriablePart, Lme/mamiiblt/instafel/utils/OpenIflMenu;"
                        fContent[i + 2] = "    invoke-direct {$veriablePart}, Lme/mamiiblt/instafel/utils/OpenIflMenu;-><init>()V"

                        Log.info("new-instance & invoke-direct lines modified at $i & ${i + 2} lines")
                        lock = true
                    }
                }

                if (lock) {
                    FileUtils.writeLines(longClickClass, fContent)
                    success("Lines modified successfully.")
                } else {
                    failure("instance and invoke lines couldn't found")
                }
            }
        }
    )
}