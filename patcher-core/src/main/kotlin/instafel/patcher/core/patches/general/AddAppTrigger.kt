package instafel.patcher.core.patches.general

import instafel.patcher.core.utils.FileSearchResult
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SearchUtils
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Add App Trigger",
    shortname = "add_app_trigger",
    desc = "This patch must be applied for Instafel Stuffs",
    isSingle = false
)
class AddAppTrigger: InstafelPatch() {

    lateinit var interfaceFile: File
    lateinit var interfaceClassName: String
    lateinit var activityFile: File

    override fun initializeTasks() = mutableListOf(
        @PInfos.TaskInfo("Find getRootContent() method")
        object: InstafelTask() {
            override fun execute() {
                when (val result = SearchUtils.getFileContainsAllCords(smaliUtils,
                    listOf(
                        ".method public getRootActivity()Landroid/app/Activity;",
                    ))) {
                    is FileSearchResult.Success -> {
                        interfaceFile = result.file
                        interfaceClassName = interfaceFile.name.substringBefore(".")
                        Log.info("Interface class name is $interfaceClassName")
                        success("Interface class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Find activity")
        object: InstafelTask() {
            override fun execute() {
                when (val result = SearchUtils.getFileContainsAllCords(smaliUtils,
                    listOf(
                        "Landroid/content/res/Configuration;",
                        "Lcom/facebook/quicklog/reliability/UserFlowLogger",
                        "Lcom/instagram/quickpromotion/intf/QPTooltipAnchor",
                        ".super Ljava/lang/Object;",
                        "MainFeedQuickPromotionDelegate.onCreateView"
                    ))) {
                    is FileSearchResult.Success -> {
                        activityFile = result.file
                        success("Activity class found successfully")
                    }
                    is FileSearchResult.NotFound -> {
                        failure("Patch aborted because no any classes found.")
                    }
                }
            }
        },
        @PInfos.TaskInfo("Add trigger to activity")
        object: InstafelTask() {
            override fun execute() {
                val fContent = smaliUtils.getSmaliFileContent(activityFile.absolutePath).toMutableList()

                Log.info("Searching reference line...")
                val invokeLines = mutableListOf<String>()
                var status = false

                fContent.forEachIndexed { i, line ->
                    if (line.contains("invoke-direct") &&
                        line.contains("Landroidx/fragment/app/Fragment") &&
                        !line.contains("Lcom/instagram/quickpromotion/intf/QuickPromotionSlot;")
                    ) {
                        Log.info("Invoke line found in line $i, ${line.trim()}")
                        invokeLines.add(line)
                    }
                }

                if (invokeLines.size != 1) {
                    failure("invokeLines size is more or equal to 0")
                    return
                }

                lateinit var callerLines: Array<String>
                val invokeLine = invokeLines.first()
                val regex = """\{([^}]*)}""".toRegex()
                val match = regex.find(invokeLine)

                val variablesArr = match?.groups?.get(1)?.value?.split(", ")
                    ?: run {
                        failure("Cannot parse variables in invoke-direct line: $invokeLine")
                        return
                    }

                callerLines = arrayOf(
                    "    invoke-virtual {${variablesArr[1]}}, LX/$interfaceClassName;->getRootActivity()Landroid/app/Activity;",
                    "",
                    "    move-result-object v0",
                    "",
                    "    invoke-static {v0}, Lme/mamiiblt/instafel/utils/InitializeInstafel;->triggerCheckUpdates(Landroid/app/Activity;)V",
                    ""
                )
                Log.info("Caller lines set successfully.")
                val newFileContent = fContent.toMutableList()

                fContent.forEachIndexed { i, line ->
                    if (line.contains("iput-object") &&
                        fContent[i + 2].contains("return-void")) {

                        Log.info("Method end found at line ${i + 2}")

                        var sVal = i + 2
                        callerLines.forEach { callerLine ->
                            newFileContent.add(sVal, callerLine)
                            sVal++
                        }

                        status = true
                    }
                }

                fContent.clear()
                fContent.addAll(newFileContent)

                if (status) {
                    FileUtils.writeLines(activityFile, fContent)
                    success("Caller lines added into Main Activity successfully")
                } else {
                    failure("Patcher can't find correct lines...")
                }
            }
        }
    )
}