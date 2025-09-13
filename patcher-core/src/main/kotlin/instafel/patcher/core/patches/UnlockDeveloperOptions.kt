package instafel.patcher.core.patches

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.Log
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
    author = "mamiiblt",
    isSingle = true
)
class UnlockDeveloperOptions: InstafelPatch() {

    lateinit var className: String
    lateinit var unlockRefSmali: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find reference smali file in X folders")
        object: InstafelTask() {
            override fun execute() {
                var fileFoundLock = false

                for (folder in smaliUtils.smaliFolders) {
                    if (fileFoundLock) break

                    val xFolder = File(Utils.mergePaths(folder.absolutePath, "X"))
                    Log.info("Searching in X folder of ${folder.name}")

                    val fileIterator = FileUtils.iterateFiles(xFolder, null, true)
                    while (fileIterator.hasNext()) {
                        val file = fileIterator.next()
                        val fContent = smaliUtils.getSmaliFileContent(file.absolutePath)

                        val conditions = booleanArrayOf(false, false, false, false)
                        fContent.forEach { line ->
                            when {
                                line.contains(".field public final") && line.contains(":Lcom/google/common/collect/EvictingQueue;") -> conditions[0] = true
                                line.contains(".field public final") && line.contains(":Lcom/instagram/common/session/UserSession;") -> conditions[1] = true
                                line.contains(".field public") && line.contains(":Ljava/lang/String;") -> conditions[2] = true
                                line.contains(".super LX/") -> conditions[3] = true
                            }
                        }

                        val passStatus = conditions.all { it }

                        if (passStatus) {
                            unlockRefSmali = file
                            Log.info("File found in ${unlockRefSmali.name} at ${folder.name}")
                            fileFoundLock = true
                            break
                        }
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