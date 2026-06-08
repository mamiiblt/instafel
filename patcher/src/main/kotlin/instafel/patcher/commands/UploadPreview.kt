/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.commands

import instafel.patcher.handlers.Command
import instafel.patcher.handlers.CoreHandler
import instafel.patcher.utils.Log
import instafel.patcher.utils.Utils
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

class UploadPreview: Command {
    override fun execute(args: Array<String>) {
        try {
            if (args.count() == 0) {
                Log.severe("Missing arguments")
                exitProcess(-1)
            }

            val srcFolder = File(Paths.get(Utils.USER_DIR, args[0]).toString())

            Thread {
                CoreHandler.invokeKotlinObjectWithParams(
                    "jobs.UploadPreview",
                    "runJob",
                    arrayOf(
                        srcFolder,
                        Utils.PROP_CLI_VERSION,
                        Utils.PROP_CORE_COMMIT
                    )
                )
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while running command")
            exitProcess(-1)
        }
    }
}