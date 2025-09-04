package instafel.patcher.cli.commands

import instafel.patcher.cli.handlers.Command
import instafel.patcher.cli.handlers.CoreHandler
import instafel.patcher.cli.utils.Log
import instafel.patcher.cli.utils.Utils
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

class BuildCmd: Command {
    override fun execute(args: Array<String>) {
        try {
            if (args.count() == 0) {
                Log.info("Wrong command usage type, use like that;")
                Log.info("java -jar patcher.jar build <folder>")
                return
            }

            val srcFolder = File(Paths.get(Utils.USER_DIR, args[0]).toString())

            if (!srcFolder.exists()) {
                Log.severe("The specified folder does not exist: ${srcFolder.absolutePath}")
                return
            }

            Thread {
                CoreHandler.invokeKotlinObjectWithParams(
                    "jobs.BuildProject",
                    "runJob",
                    arrayOf(
                        srcFolder,
                        Utils.PROP_CORE_COMMIT,
                        Utils.PROP_CLI_PROJECT_TAG,
                        Utils.PROP_CLI_VERSION
                    )
                )
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("Error while running command")
            exitProcess(-1)
        }
    }
}