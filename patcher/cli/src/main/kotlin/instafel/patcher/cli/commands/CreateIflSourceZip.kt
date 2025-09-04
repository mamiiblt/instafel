package instafel.patcher.cli.commands

import instafel.patcher.cli.handlers.Command
import instafel.patcher.cli.handlers.CoreHandler
import instafel.patcher.cli.utils.Log
import instafel.patcher.cli.utils.Utils
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

class CreateIflSourceZip: Command {
    override fun execute(args: Array<String>) {
        try {
            if (args.count() == 0) {
                Log.info("Wrong commage usage type, use like that;");
                Log.info("java -jar patcher.jar init instagram.apk");
                return;
            }

            if (!args[0].contains(".apk")) {
                Log.warning("Please select an .apk file");
                return;
            }
            Thread {
                CoreHandler.invokeKotlinObjectWithParams(
                    "jobs.CreateIflZip",
                    "runJob",
                    arrayOf(
                        File(Paths.get(Utils.USER_DIR, args[0]).toString()
                        ).absolutePath
                    )
                )
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.info("An error occurred while running command")
            exitProcess(-1)
        }
    }
}