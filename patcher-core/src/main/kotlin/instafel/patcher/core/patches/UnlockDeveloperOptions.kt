/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.core.patches

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.modals.LineData
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos

@PInfos.PatchInfo(
    name = "Unlock Developer Options",
    shortname = "unlock_developer_options",
    desc = "You can unlock developer options with applying this patch!",
    isSingle = true
)
class UnlockDeveloperOptions: InstafelPatch() {

    lateinit var className: String

    // Fallback chain: try each reference class in order until one yields a
    // validated A00(UserSession)Z target. A reference is skipped (not fatal)
    // on ambiguous/missing file, no candidates, or zero valid candidates.
    private val referenceClasses = listOf(
        "/com/instagram/notifications/push/ClearNotificationReceiver.smali",
        "/com/instagram/base/activity/BaseFragmentActivity.smali",
        "/com/instagram/business/promote/activity/PromoteActivity.smali"
    )

    override fun initializeTasks() = mutableListOf(

        @PInfos.TaskInfo("Get constraint definition class")
        object: InstafelTask() {
            override fun execute() {

                var resolvedClassName: String? = null

                for (refPath in referenceClasses) {

                    val refResults = smaliUtils.getSmaliFilesByName(refPath)

                    if (refResults.isEmpty() || refResults.size > 1) {
                        Log.info("Reference $refPath ambiguous or missing (${refResults.size} matches), trying next.")
                        continue
                    }

                    val referenceFileContent =
                        smaliUtils.getSmaliFileContent(refResults.first().absolutePath)

                    val candidates = referenceFileContent.mapIndexedNotNull { index, line ->
                        val t = line.trim()
                        if (t.contains("invoke-static") &&
                            t.contains("->A00(") &&
                            t.contains("Lcom/instagram/common/session/UserSession;") &&
                            t.contains(")Z")
                        ) LineData(index, line) else null
                    }

                    if (candidates.isEmpty()) {
                        Log.info("No invoke-static A00(UserSession)Z found in $refPath, trying next reference.")
                        continue
                    }

                    var validLine: LineData? = null

                    for (lineData in candidates) {
                        val instruction = SmaliParser.parseInstruction(lineData.content, lineData.num)
                        val extractedClass = instruction.className.replace("LX/", "").replace(";", "")

                        val targetFile = smaliUtils.getSmaliFilesByName("X/$extractedClass.smali")
                            .firstOrNull() ?: continue

                        val targetContent = smaliUtils.getSmaliFileContent(targetFile.absolutePath)

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
                        Log.info("No candidate validated in $refPath, trying next reference.")
                        continue
                    }

                    val callLineInstruction = SmaliParser.parseInstruction(validLine.content, validLine.num)
                    resolvedClassName = callLineInstruction.className.replace("LX/", "").replace(";", "")
                    break
                }

                if (resolvedClassName == null) {
                    failure("No valid DevOptions class found across any reference class.")
                    return
                }

                className = resolvedClassName
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

                // Bound the search to the A00 method body — sibling methods (A01, etc.)
                // in the same file can share the exact same move-result/return shape.
                val methodStart = devOptionsContent.indexOfFirst { line ->
                    val t = line.trim()
                    t.contains(".method") &&
                    t.contains("A00") &&
                    t.contains("Lcom/instagram/common/session/UserSession;") &&
                    t.contains(")Z")
                }

                if (methodStart == -1) {
                    failure("A00(UserSession)Z method not found in $className")
                    return
                }

                val methodEnd = (methodStart until devOptionsContent.size)
                    .firstOrNull { devOptionsContent[it].trim() == ".end method" }

                if (methodEnd == null) {
                    failure("Could not find end of A00 method body")
                    return
                }

                val moveResultIndices = (methodStart..methodEnd)
                    .filter { devOptionsContent[it].trim() == "move-result v0" }

                if (moveResultIndices.size != 1) {
                    failure("Move result line count within A00 is ${moveResultIndices.size}, expected exactly 1.")
                    return
                }

                val moveResultIndex = moveResultIndices.first()

                if (devOptionsContent[moveResultIndex + 2].contains("const v0, 0x1")) {
                    Log.info("Developer options already unlocked.")
                    success("Developer options already unlocked, skipping.")
                    return
                }

                devOptionsContent.add(moveResultIndex + 1, "    ")
                devOptionsContent.add(moveResultIndex + 2, "    const v0, 0x1")

                smaliUtils.writeContentIntoFile(devOptionsFile.absolutePath, devOptionsContent)

                Log.info("Constraint added successfully.")
                success("Developer options unlocked successfully.")
            }
        }
    )
}