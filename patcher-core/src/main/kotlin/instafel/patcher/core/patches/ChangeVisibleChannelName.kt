package instafel.patcher.core.patches

import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Change Visible Channel Name",
    shortname = "change_channel_name",
    desc = "Change visible channel name in Developer Options",
    isSingle = true
)
class ChangeVisibleChannelName: InstafelPatch() {

    lateinit var constFile: File
    var searchConstStrings = listOf( "\"NONE\"", "\"ALPHA\"", "\"BETA\"", "\"PROD\"")

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find const definition class in X classes")
        object: InstafelTask() {
            override fun execute() {
                var scannedFileSize = 0
                val cachePath = Env.Project.pVClassPath.ifEmpty { "NFN" }

                if (cachePath != "NFN") {
                    constFile = File(cachePath)
                    success("File path cached in project dir")
                    return
                }

                val foundFiles = mutableListOf<File>()

                for (folder in smaliUtils.smaliFolders) {
                    val xFolder = File(Utils.mergePaths(folder.absolutePath, "X"))
                    if (!xFolder.exists() || !xFolder.isDirectory) continue

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
                        Log.info("File name is ${foundFiles[0]}")
                        constFile = foundFiles[0]
                        Env.Project.pVClassPath = constFile.absolutePath
                        success("Const definition class successfully found in X files")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Change string constraints in file")
        object: InstafelTask() {
            override fun execute() {
                val sContent = smaliUtils.getSmaliFileContent(constFile.absolutePath).toMutableList()

                sContent.forEachIndexed { i, line ->
                    searchConstStrings.forEach { searchConst ->
                        if (line.contains(searchConst)) {
                            val iflVer = Env.Project.iflVersion
                            val changeToStr = if (iflVer == 0) "Instafel" else "Instafel v$iflVer "
                            sContent[i] = line.replace(searchConst, "\"$changeToStr\"")
                            Log.info("Constraint $searchConst found at line $i")
                        }
                    }
                }

                FileUtils.writeLines(constFile, sContent)
                success("All changeable channel name constrains updated successfully.")
            }
        }
    )
}