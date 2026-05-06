package instafel.patcher.core.patches

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.modals.LineData
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
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

                // NEW ORDER:
                // ClearNotificationReceiver → BaseFragmentActivity → PromoteActivity

                var unlockRefClassResults =
                    smaliUtils.getSmaliFilesByName("/com/instagram/notifications/push/ClearNotificationReceiver.smali")

                if (unlockRefClassResults.isEmpty() || unlockRefClassResults.size > 1) {

                    unlockRefClassResults =
                        smaliUtils.getSmaliFilesByName("/com/instagram/base/activity/BaseFragmentActivity.smali")

                    if (unlockRefClassResults.isEmpty() || unlockRefClassResults.size > 1) {

                        unlockRefClassResults =
                            smaliUtils.getSmaliFilesByName("/com/instagram/business/promote/activity/PromoteActivity.smali")

                        if (unlockRefClassResults.isEmpty() || unlockRefClassResults.size > 1) {
                            failure("No valid reference class found.")
                            exitProcess(-1)
                        }
                    }
                }

                unlockRefSmali = unlockRefClassResults.first()

                val referenceFileContent =
                    smaliUtils.getSmaliFileContent(unlockRefSmali.absolutePath)

                // collect candidates
                val candidates = referenceFileContent.mapIndexedNotNull { index, line ->
                    val t = line.trim()
                    if (t.contains("invoke-static") &&
                        t.contains("->A00(") &&
                        t.contains("Lcom/instagram/common/session/UserSession;") &&
                        t.contains(")Z")
                    ) {
                        LineData(index, line)
                    } else null
                }

                if (candidates.isEmpty()) {
                    failure("No invoke-static A00(UserSession)Z found")
                    return
                }

                // validate correct class
                var validLine: LineData? = null

                for (lineData in candidates) {

                    val instruction = SmaliParser.parseInstruction(lineData.content, lineData.num)
                    val extractedClass = instruction.className
                        .replace("LX/", "")
                        .replace(";", "")

                    val targetFile = smaliUtils
                        .getSmaliFilesByName("X/$extractedClass.smali")
                        .firstOrNull() ?: continue

                    val targetContent =
                        smaliUtils.getSmaliFileContent(targetFile.absolutePath)

                    val isValid = targetContent.any {
                        val t = it.trim()
                        t.contains(".method") &&
                        t.contains("A00") &&
                        t.contains("Lcom/instagram/common/session/UserSession;") &&
                        t.contains(")Z")
                    }

                    if (isValid) {
                        validLine = lineData
                        break
                    }
                }

                if (validLine == null) {
                    failure("No valid DevOptions class found after validation")
                    return
                }

                val callLineInstruction =
                    SmaliParser.parseInstruction(validLine.content, validLine.num)

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
