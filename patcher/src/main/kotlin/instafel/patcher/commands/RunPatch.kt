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