package instafel.patcher.cli.commands

import instafel.patcher.cli.handlers.Command
import instafel.patcher.cli.handlers.CoreHandler
import instafel.patcher.cli.utils.Log
import instafel.patcher.cli.utils.Utils
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

class RunPatch: Command {
    override fun execute(args: Array<String>) {
        try {
            if (args.count() == 0) {
                Log.info("Wrong command usage, use like that:")
                Log.info("java -jar patcher.jar run <wdir> <patch_name> [<patch_name> ...]")
                return;
            }

            val projectDir = File(Paths.get(Utils.USER_DIR, args[0]).toString());
            if (!projectDir.exists()) {
                Log.severe("Working directory doesn't exists: $projectDir")
                return;
            }

            Thread {
                CoreHandler.invokeKotlinObjectWithParams(
                    "jobs.RunPatches",
                    "runJob",
                    arrayOf(
                        projectDir,
                        args.copyOf()
                    )
                )
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("Error while running patches")
            exitProcess(-1)
        }
    }
}