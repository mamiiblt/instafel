package instafel.patcher.commands

import com.google.gson.Gson
import instafel.patcher.handlers.Command
import instafel.patcher.handlers.CoreHandler
import instafel.patcher.utils.Log
import instafel.patcher.utils.modals.PatchGroupInfo
import instafel.patcher.utils.modals.PatchInfo
import instafel.patcher.utils.modals.PatchesInfo
import kotlin.system.exitProcess

class ListPatches: Command {

    val gson = Gson()

    override fun execute(args: Array<String>) {
        try {
            var getRaw = false;
            for (arg in args) {
                if (arg.equals("--getRaw")) {
                    getRaw = true;
                }
            }

            val jsonRaw = CoreHandler.getEntryAsString("patches.json");
            val patchInfos = gson.fromJson(jsonRaw, PatchesInfo::class.java)

            if (getRaw) {
                println(jsonRaw)
                return;
            }

            println("Single Patches:")
            patchInfos.singles.forEach { patchInfo ->
                printPatch(patchInfo)
            }
            println();

            patchInfos.groups.forEach { groupInfo ->
                println("${groupInfo.name}:")
                println("    Description: ${groupInfo.desc}")
                println("    Shortname: ${groupInfo.shortname}")
                println("    Patches:")
                printGroupPatches(groupInfo)
                println()
            }

            println("Use run <wdir> <shortname>... for executing patches / patch groups")
            println("Totally found ${patchInfos.totalPatchSize} patch.")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while loading patch list from core")
            exitProcess(-1)
        }
    }

    fun printGroupPatches(patchGroupInfo: PatchGroupInfo) {
        patchGroupInfo.patches.forEach { patchGroup ->
            println("        - ${patchGroup.name}")
            println("            Shortname: ${patchGroup.shortname}")
            println("            Description: ${patchGroup.desc}")
            println("            Tasks:")

            patchGroup.tasks.forEach { task -> println("                - $task") }
        }
    }

    fun printPatch(patch: PatchInfo) {

        println("    • ${patch.name}")
        println("        Shortname: ${patch.shortname}")
        println("        Description: ${patch.desc}")

        if (patch.tasks.isEmpty()) {
            println("          Tasks: (none)")
            return;
        }

        println("          Tasks:")
        patch.tasks.forEach { taskName -> println("              - $taskName") }
    }
}