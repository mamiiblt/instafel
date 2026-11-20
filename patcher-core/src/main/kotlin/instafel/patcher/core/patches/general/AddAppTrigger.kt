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
    lateinit var targetFile: File

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
                // IG 458.0.0.0.3 moved the ModalActivity launch out of this class into a
                // helper, so the activity file no longer calls getRootActivity() itself.
                // Anchor on the stable cords only; the getRootActivity() call is resolved
                // by the next task (directly, or via the helper class).
                when (val result = runBlocking {
                    SearchUtils.getFileContainsAllCords(
                        smaliUtils,
                        listOf(
                            listOf("LeaveCloseFriendsListMutation"),
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

        @PInfos.TaskInfo("Find trigger target")
        object : InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(activityFile.absolutePath)

                // Old IG (<= 458.0.0.0.0): the activity itself calls getRootActivity(),
                // inject the trigger there directly.
                if (fContent.any { it.contains("getRootActivity()Landroid/app/Activity") }) {
                    targetFile = activityFile
                    Log.info("Activity calls getRootActivity() directly, using it as target")
                    success("Trigger target is the activity itself")
                    return
                }

                // New IG (>= 458.0.0.0.3): the launch moved to a helper class invoked
                // from the entry_point method. Resolve that helper and inject there.
                val helperFile = findHelperFile(fContent)
                if (helperFile != null) {
                    targetFile = helperFile
                    Log.info("Selected helper file: ${targetFile.name}")
                    success("Helper class found successfully")
                } else {
                    failure("Patch aborted because no helper class found.")
                }
            }

            private fun findHelperFile(fContent: List<String>): File? {
                // Locate the method that sets up the entry_point Bundle.
                var methodStart = -1
                var methodEnd = -1
                var currentMethodStart = -1
                var entryPointInMethod = false
                for (i in fContent.indices) {
                    val trimmed = fContent[i].trim()
                    if (trimmed.startsWith(".method")) {
                        currentMethodStart = i
                        entryPointInMethod = false
                    } else if (trimmed.startsWith(".end method")) {
                        if (entryPointInMethod) {
                            methodStart = currentMethodStart
                            methodEnd = i
                            break
                        }
                        currentMethodStart = -1
                    } else if (trimmed.contains("entry_point")) {
                        entryPointInMethod = true
                    }
                }
                if (methodStart == -1) return null

                // The helper is a static call inside that method. Try each target class
                // and keep the one whose file launches ModalActivity via getRootActivity().
                val classRegex = Regex("""L([A-Za-z0-9/_$]+);->""")
                for (i in methodStart..methodEnd) {
                    val line = fContent[i]
                    if (!line.contains("invoke-static")) continue
                    val match = classRegex.find(line) ?: continue
                    val simpleName = match.groupValues[1].substringAfterLast("/")
                    val candidates = smaliUtils.getSmaliFilesByName("$simpleName.smali")
                    for (candidate in candidates) {
                        val content = smaliUtils.getSmaliFileContent(candidate.absolutePath)
                        if (content.any { it.contains("getRootActivity()Landroid/app/Activity") } &&
                            content.any { it.contains("Lcom/instagram/modal/ModalActivity;") }
                        ) {
                            return candidate
                        }
                    }
                }
                return null
            }
        },

        @PInfos.TaskInfo("Add trigger to activity")
        object : InstafelTask() {
            override fun execute() {

                val fContent = smaliUtils
                    .getSmaliFileContent(targetFile.absolutePath)
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

                    if (trimmed.startsWith(".method")) {
                        insideMethod = true
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
                    FileUtils.writeLines(targetFile, newFileContent)
                    success("Trigger injected successfully")
                } else {
                    failure("Patcher can't find correct lines...")
                }
            }
        }
    )
}
