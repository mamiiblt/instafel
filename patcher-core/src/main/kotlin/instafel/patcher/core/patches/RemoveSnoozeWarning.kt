package instafel.patcher.core.patches

import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.modals.FileSearchResult
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Remove Snooze Warning",
    shortname = "remove_snooze_warning",
    desc = "You remove build exp warning fragment with this patch",
    isSingle = true
)
class RemoveSnoozeWarning: InstafelPatch() {

    lateinit var callerClass: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find caller file")
        object: InstafelTask() {
            override fun execute() {
                when (val result = runBlocking {
                    SearchUtils.getFileContainsAllCords(smaliUtils,
                        listOf(
                            listOf("invoke-direct/range", "Lcom/instagram/release/lockout/DogfoodingEligibilityApi"),
                        ))
                }) {
                    is FileSearchResult.Success -> {
                        callerClass = result.file
                        success("Class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Find and delete correct invoker line")
        object: InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(callerClass.absolutePath).toMutableList()
                val invokeLines = smaliUtils.getContainLines(fContent,
                    "invoke-direct/range",
                    "Lcom/instagram/release/lockout/DogfoodingEligibilityApi;")

                if (invokeLines.size != 1) {
                    failure("Correct invoke-direct/range line couldn't found.")
                }

                fContent.removeAt(invokeLines[0].num)
                FileUtils.writeLines(callerClass, fContent)
                success("Caller removed in line ${invokeLines[0].num}")
            }
        },
    )
}