package instafel.patcher.core.patches

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.collections.iterator

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
                var scannedFileSize = 0
                val foundFiles = mutableListOf<File>()
                val searchConstStrings = listOf(
                    "Lcom/instagram/release/lockout/DogfoodingEligibilityApi",
                    "invoke-direct/range",
                    "null cannot be cast to non-null type"
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
                        callerClass = foundFiles[0]
                        success("Activity file found in X files successfully")
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