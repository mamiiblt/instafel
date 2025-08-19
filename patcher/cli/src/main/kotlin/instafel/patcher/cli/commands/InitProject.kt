package instafel.patcher.cli.commands

import instafel.patcher.cli.handlers.Command
import instafel.patcher.cli.handlers.CoreHandler
import instafel.patcher.cli.utils.Log
import instafel.patcher.cli.utils.Utils
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

class InitProject: Command {
    override fun execute(args: Array<String>) {
        try {
            if (args.count() != 0) {
                val fileArgument = args.first()
                if (fileArgument.contains(".apk") || fileArgument.contains(".zip")) {
                    val apkFile = File(Paths.get(Utils.USER_DIR, fileArgument).toString())

                    if (!apkFile.exists()) {
                        Log.severe("The specified file does not exist: ${apkFile.absolutePath}")
                        return;
                    }

                    val clazz = CoreHandler.CORE_CLASS_LOADER.loadClass("me.mamiiblt.instafel.patcher.core.jobs.InitializeProject")
                    val method = clazz.getMethod("runInitProject", File::class.java, File::class.java)
                    val projectDir = File(Utils.USER_DIR)

                } else {
                    Log.warning("Please select an .apk file")
                }
            } else {
                Log.info("Wrong commage usage type, use like that;")
                Log.info("java -jar patcher.jar init instagram.apk")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while running tasks")
            exitProcess(-1)
        }
    }
}