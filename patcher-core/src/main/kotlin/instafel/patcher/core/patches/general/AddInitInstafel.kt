package instafel.patcher.core.patches.general

import instafel.patcher.core.source.SmaliParser
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.patch.InstafelPatch
import instafel.patcher.core.utils.patch.InstafelTask
import instafel.patcher.core.utils.patch.PInfos
import org.apache.commons.io.FileUtils
import kotlin.system.exitProcess

@PInfos.PatchInfo(
    name = "Add Initialize Instafel",
    shortname = "add_init_instafel",
    desc = "This patch must be applied for Instafel Menu",
    isSingle = false
)
class AddInitInstafel: InstafelPatch() {

    override fun initializeTasks() = mutableListOf<InstafelTask> (
        @PInfos.TaskInfo("Find getRootContent() method")
        object: InstafelTask() {
            override fun execute() {
                val appShellResult = smaliUtils.getSmaliFilesByName("/com/instagram/app/InstagramAppShell.smali")
                val appShellFile = if (appShellResult.isEmpty() || appShellResult.size > 1) {
                    failure("InstagramAppShell file can't be found / selected.")
                    exitProcess(-1)
                } else {
                    appShellResult.first()
                }

                val fContent = smaliUtils.getSmaliFileContent(appShellFile.absolutePath).toMutableList()

                var onCreateMethodLine = 0
                var lock = false

                for (i in fContent.indices) {
                    val line = fContent[i]

                    if (line.contains("onCreate()V") && line.contains(".method")) {
                        onCreateMethodLine = i
                    }

                    if (line.contains("Landroid/app/Application;->onCreate()V")) {
                        if (onCreateMethodLine == 0) {
                            Log.severe("onCreateMethod cannot found before caller.")
                        }

                        val callerInstruction = SmaliParser.parseInstruction(line, i)
                        val onCreateVariableName = callerInstruction.registers[0]

                        val unusedRegister = smaliUtils.getUnusedRegistersOfMethod(fContent, onCreateMethodLine, i)
                        Log.info("Unused register is v$unusedRegister before line $i in onCreate method")

                        val content = listOf(
                            "    invoke-static {$onCreateVariableName}, Lme/mamiiblt/instafel/utils/InitializeInstafel;->setContext(Landroid/app/Application;)V",
                            "    new-instance v$unusedRegister, Lme/mamiiblt/instafel/utils/InstafelCrashHandler;",
                            "    invoke-direct {v$unusedRegister, $onCreateVariableName}, Lme/mamiiblt/instafel/utils/InstafelCrashHandler;-><init>(Landroid/content/Context;)V",
                            "    invoke-static {v$unusedRegister}, Ljava/lang/Thread;->setDefaultUncaughtExceptionHandler(Ljava/lang/Thread\$UncaughtExceptionHandler;)V"
                        )

                        if (fContent[i + 2].contains("Lme/mamiiblt/instafel")) {
                            failure("This patch is applied already.")
                        }

                        fContent.add(i + 2, content.joinToString("\n\n") + "\n")
                        lock = true
                    }
                }

                if (lock) {
                    FileUtils.writeLines(appShellFile, fContent)
                    success("Initializer lines added successfully.")
                } else {
                    failure("onCreate() method not found.")
                }
            }
        }
    )
}