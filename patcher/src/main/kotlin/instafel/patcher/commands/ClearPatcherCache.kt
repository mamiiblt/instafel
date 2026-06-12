/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.commands

import instafel.patcher.handlers.Command
import instafel.patcher.utils.Log
import instafel.patcher.utils.Utils
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