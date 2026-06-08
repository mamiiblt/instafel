/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.core.patches.general

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.modals.FileSearchResult
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Add App Trigger",
    shortname = "add_app_trigger",
    desc = "This patch must be applied for Instafel Stuffs",
    isSingle = false
)
class AddAppTrigger : InstafelPatch() {

    lateinit var activityFile: File

    override fun initializeTasks() = mutableListOf(

        
        @PInfos.TaskInfo("Find getRootActivity interface")
        object : InstafelTask() {
            override fun execute() {
                when (val result = runBlocking {
                    SearchUtils.getFileContainsAllCords(
                        smaliUtils,
                        listOf(
                            listOf(".method public getRootActivity()Landroid/app/Activity;")
                        )
                    )
                }) {
                    is FileSearchResult.Success -> {
                        Log.info("Interface class: ${result.file.name}")
                        success("Interface method found")
                    }

                    is FileSearchResult.NotFound -> {
                        Log.info("Interface not found (ignored)")
                        success("Skipping interface check")
                    }
                }
            }
        },

        
        @PInfos.TaskInfo("Find activity")
        object : InstafelTask() {
            override fun execute() {

                when (val result = runBlocking {
                    SearchUtils.getFileContainsAllCords(
                        smaliUtils,
                        listOf(
                            listOf("LeaveCloseFriendsListMutation"),
                            listOf("getRootActivity()Landroid/app/Activity"),
                            listOf("Lcom/instagram/modal/ModalActivity;"),
                            listOf("entry_point")
                        )
                    )
                }) {
                    is FileSearchResult.Success -> {
                        activityFile = result.file
                        Log.info("Selected file: ${activityFile.name}")
                        success("Activity class found successfully")
                    }

                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no matching file found.")
                    }
                }
            }
        },

        
        @PInfos.TaskInfo("Add trigger to activity")
        object : InstafelTask() {
            override fun execute() {

                val fContent = smaliUtils
                    .getSmaliFileContent(activityFile.absolutePath)
                    .toMutableList()

                val newFileContent = fContent.toMutableList()

                var status = false
                var insertOffset = 0
                var insideMethod = false

                
                if (fContent.any { it.contains("InitializeInstafel;->triggerCheckUpdates") }) {
                    Log.info("Already patched, skipping...")
                    success("Already patched")
                    return
                }

                fContent.forEachIndexed { i, line ->

                    val trimmed = line.trim()

    
                    if (trimmed.startsWith(".method") && trimmed.contains("A00(")) {
                        insideMethod = true
                        Log.info("Entered A00 method")
                        return@forEachIndexed
                    }

                    
                    if (insideMethod && trimmed.startsWith(".end method")) {
                        insideMethod = false
                        return@forEachIndexed
                    }

                    if (insideMethod && trimmed.contains("getRootActivity()Landroid/app/Activity")) {

                        
                        for (k in 1..5) {
                            val idx = i + k
                            if (idx >= fContent.size) break

                            val checkLine = fContent[idx].trim()

                            if (checkLine.startsWith("move-result-object")) {

                                val register = checkLine.split(" ").last()

                                Log.info("Injection point at line ${idx + 1} using $register")

                                val callerLines = arrayOf(
                                    "    invoke-static {$register}, Linstafel/app/utils/InitializeInstafel;->triggerCheckUpdates(Landroid/app/Activity;)V",
                                    "",
                                    "    invoke-static {$register}, Linstafel/app/utils/InitializeInstafel;->triggerUploadMapping(Landroid/app/Activity;)V",
                                    "",
                                )

                                var insertIndex = idx + 1 + insertOffset

                                callerLines.forEach {
                                    newFileContent.add(insertIndex, it)
                                    insertIndex++
                                }

                                insertOffset += callerLines.size
                                status = true
                                break
                            }
                        }
                    }
                }

                if (status) {
                    FileUtils.writeLines(activityFile, newFileContent)
                    success("Trigger injected successfully")
                } else {
                    failure("Patcher can't find correct lines...")
                }
            }
        }
    )
}
