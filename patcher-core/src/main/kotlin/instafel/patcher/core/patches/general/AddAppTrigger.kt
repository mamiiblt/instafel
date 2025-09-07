package instafel.patcher.core.patches.general

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import java.io.File

@PInfos.PatchInfo(
    name = "Add App Trigger",
    shortname = "add_app_trigger",
    desc = "This patch must be applied for Instafel Stuffs",
    author = "mamiiblt",
    isSingle = false
)
class AddAppTrigger: InstafelPatch() {

    lateinit var interfaceFile: File
    lateinit var interfaceClassName: String
    lateinit var activityFile: File

    override fun initializeTasks() = mutableListOf(
        object: InstafelTask("Find getRootContent() method") {
            override fun execute() {
                var scannedFileSize = 0
                var fileFoundLock = false;

                smaliUtils.smaliFolders.forEach { folder ->
                    if (fileFoundLock) return

                    val xFolder = File(Utils.mergePaths(folder.absolutePath, "X"))
                    Log.info("Searching in X folder of " + folder.getName())

                    val fileIterator = FileUtils.iterateFiles(xFolder, null, true)
                    fileIterator.forEach { file ->
                        if (fileFoundLock) return@forEach

                        scannedFileSize++
                        val fContent = smaliUtils.getSmaliFileContent(file.absolutePath)
                        val matchLines = smaliUtils.getContainLines(
                            fContent,
                            ".method public getRootActivity()Landroid/app/Activity;"
                        )

                        if (matchLines.size == 1) {
                            interfaceFile = file
                            interfaceClassName = interfaceFile!!.name.substringBefore(".")
                            Log.info("File found in ${interfaceFile!!.name} at ${folder.name}")
                            fileFoundLock = true
                        }
                    }
                }

                if (fileFoundLock) {
                    Log.info("Totally scanned $scannedFileSize file(s) in X folders")
                    Log.info("Interface class name is $interfaceClassName")
                    interfaceClassName = interfaceFile.name.substringBefore(".")
                    success("Interface file founded.")
                } else {
                    failure("Interface file cannot be found.")
                }
            }

        },
        object: InstafelTask("Find activity") {
            override fun execute() {
                var scannedFileSize = 0
                val foundFiles = mutableListOf<File>()
                val searchConstStrings = listOf(
                    "Landroid/content/res/Configuration;",
                    "Lcom/facebook/quicklog/reliability/UserFlowLogger",
                    "Lcom/instagram/quickpromotion/intf/QPTooltipAnchor",
                    ".super Ljava/lang/Object;",
                    "MainFeedQuickPromotionDelegate.onCreateView"
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
                        activityFile = foundFiles[0]
                        success("Activity file found in X files successfully")
                    }
                }
            }
        },
        object: InstafelTask("Add trigger to activity") {
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