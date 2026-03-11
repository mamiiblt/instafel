package instafel.patcher.core.patches.general

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.modals.FileSearchResult
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Add App Trigger",
    shortname = "add_app_trigger",
    desc = "This patch must be applied for Instafel Stuffs",
    isSingle = false
)
class AddAppTrigger: InstafelPatch() {

    lateinit var interfaceFile: File
    lateinit var interfaceClassName: String
    lateinit var activityFile: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find getRootContent() method")
        object: InstafelTask() {
            override fun execute() {
                when (val result = runBlocking {
                    SearchUtils.getFileContainsAllCords(smaliUtils,
                        listOf(
                            listOf(".method public getRootActivity()Landroid/app/Activity;"),
                        ))
                }) {
                    is FileSearchResult.Success -> {
                        interfaceFile = result.file
                        interfaceClassName = interfaceFile.name.substringBefore(".")
                        Log.info("Interface class name is $interfaceClassName")
                        success("Interface class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Find activity")
        object: InstafelTask() {
            override fun execute() {
                when (val result = runBlocking {
                    SearchUtils.getFileContainsAllCords(smaliUtils,
                        listOf(
                            listOf("MainFeedQuickPromotionDelegate.onCreateView"),
                            listOf(".super", "Ljava/lang/Object;")
                        ))
                }) {
                    is FileSearchResult.Success -> {
                        activityFile = result.file
                        success("Activity class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Add trigger to activity")
        object: InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(activityFile.absolutePath).toMutableList()

                Log.info("Searching reference line...")
                var status = false

                val callerLines = arrayOf(
                    "    invoke-static {v2}, Linstafel/app/utils/InitializeInstafel;->triggerCheckUpdates(Landroid/app/Activity;)V",
                    "",
                    "    invoke-static {v2}, Linstafel/app/utils/InitializeInstafel;->triggerUploadMapping(Landroid/app/Activity;)V",
                    "",
                )

                val newFileContent = fContent.toMutableList()
                var insertOffset = 0

                fContent.forEachIndexed { i, line ->
                    if (line.trim().contains("move-result-object v2") &&
                        i > 0 &&
                        fContent[i - 1].trim().contains("getRootActivity()Landroid/app/Activity;")
                    ) {
                        Log.info("Injection point found at line ${i + 1}")
                        var sVal = i + 1 + insertOffset
                        callerLines.forEach { callerLine ->
                            newFileContent.add(sVal, callerLine)
                            sVal++
                        }
                        insertOffset += callerLines.size
                        status = true
                    }
                }

                if (status) {
                    FileUtils.writeLines(activityFile, newFileContent)
                    success("Caller lines added into Main Activity successfully")
                } else {
                    failure("Patcher can't find correct lines...")
                }
            }
        }
    )
}