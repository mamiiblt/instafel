package instafel.patcher.commands

import instafel.patcher.handlers.Command
import instafel.patcher.handlers.CoreHandler
import instafel.patcher.utils.Log
import instafel.patcher.utils.Utils
import java.nio.file.Paths
import kotlin.system.exitProcess

class ForceUpdateCore: Command {
    override fun execute(args: Array<String>) {
        try {
            Log.info("Downloading latest core...");
            val uInfo = CoreHandler.getLatestCoreUpdateInfo()
            if (Utils.PROP_CLI_VERSION == uInfo.supported_pversion) {
                Log.info("Core ${uInfo.commit}@main downloading...")
                val coreDir = Paths.get(Utils.getPatcherFolder(), "core_data")
                CoreHandler.downloadCoreJAR(
                    uInfo,
                    coreDir.resolve("core.jar").toFile()
                )
                Log.info("Core updated successfully.")
            } else {
                Log.info("Latest core isn't compatible with your patcher, please update the CLI");
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.info("An error occurred while running command");
            exitProcess(-1);
        }
    }
}