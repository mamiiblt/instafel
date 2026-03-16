package instafel.patcher.core.patches

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.modals.FileSearchResult
import instafel.patcher.core.utils.modals.LineData
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.regex.Pattern
import kotlin.system.exitProcess

@PInfos.PatchInfo(
    name = "Unlock Developer Options",
    shortname = "unlock_developer_options",
    desc = "You can unlock developer options with applying this patch!",
    isSingle = true
)
class UnlockDeveloperOptions : InstafelPatch() {

    lateinit var className: String
    lateinit var unlockRefSmali: File

    private val invokePattern: Pattern = Pattern.compile(
        "invoke-static(?:/range)?\\s+\\{[^}]+\\},\\s+L([^;]+);->A00\\(Lcom/instagram/common/session/UserSession;\\)Z"
    )

    private val referenceClassPaths = listOf(
        "/com/instagram/base/activity/BaseFragmentActivity.smali",
        "/com/instagram/business/promote/activity/PromoteActivity.smali",
        "/com/instagram/notification/ClearNotificationReceiver.smali"
    )

    override fun initializeTasks() = mutableListOf(

        @PInfos.TaskInfo("Get constraint definition class")
        object : InstafelTask() {
            override fun execute() {

                var extractedClassName: String? = null

                for (refPath in referenceClassPaths) {
                    val refResults = smaliUtils.getSmaliFilesByName(refPath)

                    if (refResults.isEmpty() || refResults.size > 1) {
                        Log.info("Reference not found or ambiguous: $refPath — trying next.")
                        continue
                    }

                    val refFile = refResults.first()
                    val refContent = smaliUtils.getSmaliFileContent(refFile.absolutePath)

                    for (line in refContent) {
                        val matcher = invokePattern.matcher(line.trim())
                        if (matcher.find()) {
                            val rawClassPath = matcher.group(1)
                            extractedClassName = rawClassPath
                                .removePrefix("LX/")
                                .removePrefix("X/")
                                .removeSuffix(";")
                            Log.info("Found invoke-static in ${refFile.name} → class: $extractedClassName")
                            unlockRefSmali = refFile
                            break
                        }
                    }

                    if (extractedClassName != null) break
                }

                if (extractedClassName == null) {
                    failure("Could not extract DevOptions class from any reference activity.")
                    exitProcess(-1)
                }

                val candidateFile = smaliUtils.getSmaliFilesByName("X/$extractedClassName.smali")
                    .firstOrNull() ?: run {
                    failure("Smali file not found for extracted class: $extractedClassName")
                    exitProcess(-1)
                }

                val candidateContent = smaliUtils.getSmaliFileContent(candidateFile.absolutePath)
                val isValidTarget = candidateContent.any { line ->
                    line.contains(".method") &&
                    line.contains("A00") &&
                    line.contains("Lcom/instagram/common/session/UserSession;") &&
                    line.contains(")Z")
                }

                if (!isValidTarget) {
                    failure("Extracted class $extractedClassName does not contain expected A00(UserSession)Z method.")
                    exitProcess(-1)
                }

                className = extractedClassName
                success("DevOptions class is $className")
            }
        },

        @PInfos.TaskInfo("Add constraint line to DevOptions class")
        object : InstafelTask() {
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
                        .also {
                            check(it.size == 1) { "Move result line size is 0 or bigger than 1" }
                        }
                        .first()

                check(
                    !devOptionsContent[moveResultLine.num + 2].contains("const v0, 0x1")
                ) { "Developer options already unlocked." }

                devOptionsContent.add(moveResultLine.num + 1, "    ")
                devOptionsContent.add(moveResultLine.num + 2, "    const v0, 0x1")

                smaliUtils.writeContentIntoFile(devOptionsFile.absolutePath, devOptionsContent)

                Log.info("Constraint added successfully.")
                success("Developer options unlocked successfully.")
            }
        }
    )
}
