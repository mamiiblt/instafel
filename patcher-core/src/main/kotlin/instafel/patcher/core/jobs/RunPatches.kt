package instafel.patcher.core.jobs

import instafel.patcher.core.PatchInfoLoader
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.modals.CLIJob
import instafel.patcher.core.source.WorkingDir
import instafel.patcher.core.utils.modals.pojo.PatchGroupInfo
import instafel.patcher.core.utils.modals.pojo.PatchInfo
import instafel.patcher.core.utils.patch.PInfos
import java.io.File
import kotlin.system.exitProcess

object RunPatches: CLIJob {

    override fun runJob(vararg args: Any) {
        val projectFolder = args.getOrNull(0) as? File
        val enteredShortnames = args.getOrNull(1) as? Array<*>

        if (projectFolder !is File || enteredShortnames?.isArrayOf<String>() != true) {
            Log.severe("Arguments must be Files")
            exitProcess(-1)
        }

        Env.PROJECT_DIR = WorkingDir.getExistsWorkingDir(projectFolder)
        Env.setupProject()
        Env.setupConfig()

        Log.info("Loading patches...")
        val runnablePatches = PatchInfoLoader.loadPatches(enteredShortnames)

        if (runnablePatches.isEmpty()) {
            Log.severe("No any patch loaded to execute")
            return;
        }
        Log.info("Executing loaded patches...")

        runnablePatches.map { patch ->
            val patchInfo = patch.key
            val patch = patch.value

            Log.info("")
            Log.info(Env.SEPARATOR_LINE)
            println(patchInfo.name)
            println(patchInfo.desc)
            println()
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

            if (patchInfo.groupShortname == "single") {
                Env.Project.appliedPatches.singlePatches.add(patchInfo)
            } else {
                val existingGroup = Env.Project.appliedPatches.groupPatches.find { it.shortname == patchInfo.groupShortname }

                if (existingGroup != null) {
                    existingGroup.patches.add(patchInfo)
                } else {
                    val coreGroupInfo = PatchInfoLoader.patchesInfo.groups.find { it.shortname == patchInfo.groupShortname }!!

                    Env.Project.appliedPatches.groupPatches.add(
                        PatchGroupInfo(
                            name = coreGroupInfo.name,
                            desc = coreGroupInfo.desc,
                            shortname = coreGroupInfo.shortname,
                            path = coreGroupInfo.path,
                            patches = mutableListOf(patchInfo)
                        )
                    )
                }
            }

            Log.info("")
            Log.info("All tasks ran successfully.")
            Log.info(Env.SEPARATOR_LINE)
        }

        Log.info("")
        Log.info("All patches executed successfully.")
        Env.saveConfig()
        Env.saveProject()
    }
}