package instafel.patcher.core.patches.clone

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Replace Instagram Strings",
    shortname = "clone_replace_strs",
    desc = "It makes app compatible for clone generation",
    author = "mamiiblt",
    isSingle = false
)
class ClonePackageReplacer: InstafelPatch() {

    val NEW_PACKAGE_NAME = "com.instafel.android"

    override fun initializeTasks() = mutableListOf<InstafelTask>(
        object: InstafelTask("Update package name in smali files") {
            override fun execute() {
                Log.info("Changing package name strings in smali files.")
                var changedLineCount = 0

                smaliUtils.smaliFolders.forEach { folder ->
                    FileUtils.iterateFiles(File(folder.absolutePath), null, true).forEach { sourcesFile ->
                        if (!sourcesFile.absolutePath.contains("/me/mamiiblt/instafel")) {
                            val cloneFile = File(sourcesFile.absolutePath.replace("sources", "clone_ref"))
                            val file = if (cloneFile.exists()) cloneFile else sourcesFile
                            val fContent = smaliUtils.getSmaliFileContent(file.absolutePath).toMutableList()
                            var fileModified = false

                            fContent.forEachIndexed { index, line ->
                                if (line.contains("\"com.instagram.android\"")) {
                                    fContent[index] = line.replace(
                                        "\"com.instagram.android\"",
                                        "\"$NEW_PACKAGE_NAME\""
                                    )
                                    Log.info("Constraint updated in ${folder.name}/${file.name} at line $index")
                                    changedLineCount++
                                    fileModified = true
                                }
                            }

                            if (fileModified) {
                                val targetFile = File(file.absolutePath.replace("sources", "clone_ref"))
                                FileUtils.writeLines(targetFile, fContent)
                            }
                        }
                    }
                }

                Log.info("Totally $changedLineCount lines updated.")
                success("Package strings successfully updated in clone references.")
            }
        }
    )
}