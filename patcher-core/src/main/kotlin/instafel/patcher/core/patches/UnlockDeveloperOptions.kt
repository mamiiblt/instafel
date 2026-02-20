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
import java.io.File
import kotlin.system.exitProcess

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
        @PInfos.TaskInfo("Get constraint definition class")
        object: InstafelTask() {
            override fun execute() {

                // 🔹 Try BaseFragmentActivity first
                var unlockRefClassResults =
                    smaliUtils.getSmaliFilesByName("/com/instagram/base/activity/BaseFragmentActivity.smali")

                if (unlockRefClassResults.isEmpty() || unlockRefClassResults.size > 1) {

                    // 🔹 Fallback to PromoteActivity
                    unlockRefClassResults =
                        smaliUtils.getSmaliFilesByName("/com/instagram/business/promote/activity/PromoteActivity.smali")

                    if (unlockRefClassResults.isEmpty() || unlockRefClassResults.size > 1) {
                        failure("Neither BaseFragmentActivity nor PromoteActivity class can be found / selected.")
                        exitProcess(-1)
                    }
                }

                unlockRefSmali = unlockRefClassResults.first()

                val referenceFileContent =
                    smaliUtils.getSmaliFileContent(unlockRefSmali.getAbsolutePath())

                var linesWithInvokeAndUserSession: List<LineData> =
                    smaliUtils.getContainLines(
                        referenceFileContent,
                        "(Lcom/instagram/common/session/UserSession;)Z",
                        "invoke-static"
                    )

                // 🔹 If no match in BaseFragmentActivity, try PromoteActivity
                if (linesWithInvokeAndUserSession.size != 1 &&
                    unlockRefSmali.absolutePath.contains("BaseFragmentActivity")
                ) {

                    val promoteResults =
                        smaliUtils.getSmaliFilesByName("/com/instagram/business/promote/activity/PromoteActivity.smali")

                    if (promoteResults.isNotEmpty()) {
                        unlockRefSmali = promoteResults.first()
                        val promoteContent =
                            smaliUtils.getSmaliFileContent(unlockRefSmali.getAbsolutePath())

                        linesWithInvokeAndUserSession =
                            smaliUtils.getContainLines(
                                promoteContent,
                                "(Lcom/instagram/common/session/UserSession;)Z",
                                "invoke-static"
                            )
                    }
                }

                if (linesWithInvokeAndUserSession.size != 1) {
                    failure("Static caller opcode can't found or more than 1!")
                    return;
                }

                val callLine: LineData = linesWithInvokeAndUserSession.get(0)
                val callLineInstruction =
                    SmaliParser.parseInstruction(callLine.content, callLine.num)

                className =
                    callLineInstruction.className.replace("LX/", "").replace(";", "")

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

                val devOptionsContent =
                    smaliUtils.getSmaliFileContent(devOptionsFile.absolutePath).toMutableList()

                val moveResultLine =
                    smaliUtils.getContainLines(devOptionsContent, "move-result", "v0")
                        .also { check(it.size == 1) { "Move result line size is 0 or bigger than 1" } }
                        .first()

                check(!devOptionsContent[moveResultLine.num + 2]
                    .contains("const v0, 0x1")) { "Developer options already unlocked." }

                devOptionsContent.add(moveResultLine.num + 1, "    ")
                devOptionsContent.add(moveResultLine.num + 2, "    const v0, 0x1")

                smaliUtils.writeContentIntoFile(devOptionsFile.absolutePath, devOptionsContent)

                Log.info("Constraint added successfully.")
                success("Developer options unlocked successfully.")
            }
        }
    )
}
