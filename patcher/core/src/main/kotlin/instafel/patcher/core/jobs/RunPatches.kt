package instafel.patcher.core.jobs

import instafel.patcher.core.PatchLoader
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.modals.CLIJob
import instafel.patcher.core.source.WorkingDir
import instafel.patcher.core.utils.patch.InstafelPatch
import java.io.File
import kotlin.reflect.full.createInstance
import kotlin.system.exitProcess

object RunPatches: CLIJob {

    override fun runJob(vararg args: Any) {
        val projectFolder = args.getOrNull(0) as? File
        val patchShortnames = args.getOrNull(1) as? Array<*>

        if (projectFolder !is File || patchShortnames?.isArrayOf<String>() != true) {
            Log.severe("Arguments must be Files")
            exitProcess(-1)
        }

        Env.PROJECT_DIR = WorkingDir.getExistsWorkingDir(projectFolder)
        Env.Project.setupProject()
        Env.Config.setupConfig()

        val runnablePatches = mutableListOf<InstafelPatch>()
        Log.info("Loading patches...")

        for (i in 1 until patchShortnames.size) {
            var findResult = false
            val shortName = patchShortnames[i] as String

            PatchLoader.findPatchByShortname(shortName)?.let { patch ->
                if (patch.isSingle) {
                    Log.info("Patch, ${patch.name} loaded")
                    runnablePatches.add(patch)
                    findResult = true
                } else {
                    Log.severe("Patch $shortName can't be runned as single patch, it is a group patch, use group name instead.")
                }
            }

            PatchLoader.findPatchGroupByShortname(shortName)?.let { group ->
                try {
                    group.loadPatches()
                    group.loadPatches()
                    group.patches.forEach { gPatch ->
                        val gnPatch = gPatch.createInstance()
                        Log.info("Patch, ${gnPatch.name} loaded from ${group.name}")
                        runnablePatches.add(gnPatch)
                    }
                    findResult = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.severe("Error while loading patches in group ${group.name}")
                }
            }

            if (!findResult) {
                Log.info("Patch $shortName is not found in patches.")
            }
        }

        if (runnablePatches.isNotEmpty()) {
            Log.info("Totally ${runnablePatches.size} patch loaded")
        } else {
            Log.severe("No any patch loaded to execute")
            exitProcess(-1)
        }

        Log.info("Executing patches...")
        for (patch in runnablePatches) {
            Log.info("")
            Log.info(Env.SEPARATOR_LINE)
            println(patch.name)
            println("by @${patch.author}")
            println(patch.description)
            println("")
            Log.info("Loading tasks..")
            try {
                patch.loadTasks()
                Log.info("${patch.tasks.size} task loaded")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.severe("Error while loading task in ${patch.name}")
            }

            Log.info("Executing tasks...")
            for (task in patch.tasks) {
                Log.info("")
                Log.info("Execute: ${task.taskName}")
                try {
                    task.execute()
                } catch (e: Exception) {
                    e.printStackTrace()
                    task.failure("Error while running task: ${e.message}")
                }
            }

            Log.info("")
            val patches = Env.Project.getString(Env.Project.Keys.APPLIED_PATCHES, "")
            Env.Project.setString(
                Env.Project.Keys.APPLIED_PATCHES,
                if (patches.isEmpty()) patches + patch.shortname else "$patches,${patch.shortname}"
            )
            Log.info("All tasks runned successfully.")
            Log.info(Env.SEPARATOR_LINE)
        }

        Log.info("")
        Log.info("All patches executed successfully.")
        Env.Project.saveProperties()
        Env.Config.saveProperties()
    }
}