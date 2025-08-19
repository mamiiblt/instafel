package instafel.patcher.cli.commands

import instafel.patcher.cli.handlers.Command
import instafel.patcher.cli.utils.Log
import instafel.patcher.cli.utils.Utils
import org.apache.commons.io.FileUtils
import java.io.File

class ClearPatcherCache: Command {
    override fun execute(args: Array<String>) {
        try {
            val cacheFolder = File(Utils.getPatcherFolder())
            Log.info("Cache folder path is ${cacheFolder.absolutePath}")
            if (cacheFolder.exists()) {
                FileUtils.deleteDirectory(cacheFolder)
                Log.info("Cache folder deleted successfully")
            } else {
                Log.severe("No any cache folder found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while clearing cache folder.")
        }
    }
}