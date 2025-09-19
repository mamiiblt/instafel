package instafel.patcher.core.patches

import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.FileSearchResult
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
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
                val cachePath = Env.Project.pVClassPath.ifEmpty { "NFN" }

                if (cachePath != "NFN") {
                    constFile = File(cachePath)
                    success("File path cached in project dir")
                    return
                }

                when (val result = SearchUtils.getFileContainsAllCords(smaliUtils,
                    listOf(
                        listOf( "\"NONE\""),
                        listOf("\"ALPHA\""),
                        listOf("\"BETA\""),
                        listOf("\"PROD\"")
                    ))) {
                    is FileSearchResult.Success -> {
                        constFile = result.file
                        success("Const definition class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
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