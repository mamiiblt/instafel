package instafel.patcher.core.patches.general

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.collections.iterator

@PInfos.PatchInfo(
    name = "Add Long Click Event",
    shortname = "add_long_click_event",
    desc = "This patch must be applied for Instafel Menu",
    author = "mamiiblt",
    isSingle = false
)
class AddLongClickEvent: InstafelPatch() {

    lateinit var longClickFile: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find long click smali file")
        object: InstafelTask() {
            override fun execute() {
                var scannedFileSize = 0
                val foundFiles = mutableListOf<File>()
                val searchConstStrings = listOf(
                    "notifications_entry_point_impression",
                    "null cannot be cast to non-null type android.content.Context"
                )

                smaliUtils.smaliFolders.forEach { folder ->
                    val xFolder = File(Utils.mergePaths(folder.absolutePath, "X"))
                    if (!xFolder.exists() || !xFolder.isDirectory) return

                    val fileIterator = FileUtils.iterateFiles(xFolder, null, true)

                    for (file in fileIterator) {
                        scannedFileSize++
                        val fContent = smaliUtils.getSmaliFileContent(file.absolutePath)
                        val passStatuses = BooleanArray(searchConstStrings.size)

                        for (line in fContent) {
                            searchConstStrings.forEachIndexed { i, str ->
                                if (line.contains(str)) passStatuses[i] = true
                            }
                        }

                        val passStatus = passStatuses.all { it }

                        if (passStatus) {
                            Log.info("A file found in ${file.name} at ${folder.name}")
                            foundFiles.add(file)
                        }
                    }
                }

                when {
                    foundFiles.isEmpty() || foundFiles.size > 1 -> {
                        failure("Found more files than one (or no any file found) for apply patch, add more condition for find correct file.")
                    }
                    else -> {
                        Log.info("Totally scanned $scannedFileSize file(s) in X folders")
                        Log.info("File name is ${foundFiles[0].name}")
                        longClickFile = foundFiles[0]
                        success("Long click smali file found in X files successfully")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Change dev options caller to Instafel menu caller")
        object: InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(longClickFile.absolutePath).toMutableList()
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
                    FileUtils.writeLines(longClickFile, fContent)
                    success("Lines modified successfully.")
                } else {
                    failure("instance and invoke lines couldn't found")
                }
            }
        }
    )
}