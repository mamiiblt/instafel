package instafel.patcher.core.patches

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.modals.FileSearchResult
import instafel.patcher.core.utils.modals.LineData
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
                        success("DogfoodingEligibilityApi caller class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Set check days duration to 0 for bypass it")
        object: InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(callerClass.absolutePath).toMutableList()

                val invokeLines = smaliUtils.getContainLines(fContent,
                    "invoke-direct/range",
                    "Lcom/instagram/release/lockout/DogfoodingEligibilityApi;")

                if (invokeLines.size != 1) {
                    failure("Correct invoke-direct/range line couldn't found.")
                }


                val longConversationMatches = mutableListOf<LineData>()

                for (i in 0 until 20) {
                    val idx = invokeLines[0].num - i
                    if (fContent[idx].contains("long-to-int")) {
                        longConversationMatches.add(LineData(idx, fContent[idx]))
                    }
                }

                if (longConversationMatches.isEmpty() || longConversationMatches.size > 1) {
                    failure("long-to-int conversation caller couldn't found.")
                }

                val regex = Regex("""long-to-int\s+(v\d+)""")
                val match = regex.find(longConversationMatches[0].content)
                val registerName = match?.groups?.get(1)?.value

                val convertedValue = "    const/4 ${registerName}, 0x0"
                Log.info("Day duration converted from '${longConversationMatches[0].content.trim()}' to '${convertedValue.trim()}'")
                fContent[longConversationMatches[0].num] = convertedValue
                FileUtils.writeLines(callerClass, fContent)
                success("Time duration successfully set to 0")
            }
        },
    )
}