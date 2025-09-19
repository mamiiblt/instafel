package instafel.patcher.core.patches

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.FileSearchResult
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.modals.LineData
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Unlock Developer Options",
    shortname = "unlock_developer_options",
    desc = "You can unlock developer options with applying this patch!",
    isSingle = true
)
class UnlockDeveloperOptions: InstafelPatch() {

    lateinit var className: String
    lateinit var unlockRefSmali: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find reference smali file in X folders")
        object: InstafelTask() {
            override fun execute() {
                when (val result = SearchUtils.getFileContainsAllCords(smaliUtils,
                    listOf(
                        listOf(".field public final", ":Lcom/google/common/collect/EvictingQueue;"),
                        listOf(".field public final", ":Lcom/instagram/common/session/UserSession;"),
                        listOf(".field public", ":Ljava/lang/String;"),
                        listOf(".super LX/"),
                    ))) {
                    is FileSearchResult.Success -> {
                        unlockRefSmali = result.file
                        success("Reference class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Get constraint definition class")
        object: InstafelTask() {
            override fun execute() {
                val referenceFileContent = smaliUtils.getSmaliFileContent(unlockRefSmali.getAbsolutePath())
                val linesWithInvokeAndUserSession: List<LineData> = smaliUtils.getContainLines(
                    referenceFileContent, "(Lcom/instagram/common/session/UserSession;)Z", "invoke-static"
                )

                if (linesWithInvokeAndUserSession.size != 1) {
                    failure("Static caller opcode can't found or more than 1!")
                    return;
                }

                val callLine: LineData = linesWithInvokeAndUserSession.get(0)
                val callLineInstruction = SmaliParser.parseInstruction(callLine.content, callLine.num)
                className = callLineInstruction.className.replace("LX/", "").replace(";", "")
                success("DevOptions class is $className")
            }
        },
        @PInfos.TaskInfo("Add constraint line to DevOptions class")
        object: InstafelTask() {
            override fun execute() {
                val devOptionsFile = smaliUtils.getSmaliFilesByName("X/$className.smali")
                    .firstOrNull() ?: run {
                    failure("Developer options file not found")
                    return
                }

                val devOptionsContent = smaliUtils.getSmaliFileContent(devOptionsFile.absolutePath).toMutableList()

                val moveResultLine = smaliUtils.getContainLines(devOptionsContent, "move-result", "v0")
                    .also { check(it.size == 1) { "Move result line size is 0 or bigger than 1" } }
                    .first()

                check(!devOptionsContent[moveResultLine.num + 2].contains("const v0, 0x1")) { "Developer options already unlocked." }

                devOptionsContent.add(moveResultLine.num + 1, "    ")
                devOptionsContent.add(moveResultLine.num + 2, "    const v0, 0x1")

                smaliUtils.writeContentIntoFile(devOptionsFile.absolutePath, devOptionsContent)

                Log.info("Constraint added successfully.")
                success("Developer options unlocked successfully.")
            }
        }
    )
}