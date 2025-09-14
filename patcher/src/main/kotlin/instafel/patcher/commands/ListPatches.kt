package instafel.patcher.commands

import instafel.patcher.handlers.Command
import instafel.patcher.handlers.CoreHandler
import instafel.patcher.utils.Log
import org.json.JSONArray
import org.json.JSONObject
import kotlin.sequences.forEach
import kotlin.system.exitProcess

class ListPatches: Command {
    override fun execute(args: Array<String>) {
        try {
            var getRaw = false;
            for (arg in args) {
                if (arg.equals("--getRaw")) {
                    getRaw = true;
                }
            }

            val patchInfos = JSONObject(CoreHandler.getEntryAsString("patches.json"))

            if (getRaw) {
                println(patchInfos.toString(2))
                return;
            }

            println("Single Patches:")
            val singlePatches = patchInfos.getJSONArray("singles")
            for (i in 0..<singlePatches.length()) {
                printPatch(singlePatches.getJSONObject(i))
            }
            println()

            val groupPatches = patchInfos.getJSONArray("groups")
            for (i in 0..<groupPatches.length()) {
                val groupInfo = groupPatches.getJSONObject(i)
                println("${groupInfo.getString("name")}:")
                println("    Description: ${groupInfo.getString("desc")}")
                println("    Shortname: ${groupInfo.getString("shortname")}")
                println("    Patches:")
                printGroupPatches(groupInfo.getJSONArray("patches"))
                println()
            }

            println("Use run <wdir> <shortname>... for executing patches / patch groups")
            println("Totally found ${patchInfos.getInt("total_patch_size")} patch.")

            /*
            if (getRaw) {
                println(patchInfos.toString(2))
            } else {
                println("Patches:")
                val singlePatches = patchInfos.getJSONArray("singles")
                for (i in 0..<singlePatches.length()) {
                    val info = singlePatches.getJSONObject(i)
                    println("    • " + getPatchInfoString(info.getString("name"), info.getString("shortname")))
                }

                println("Patch Groups:")
                val groupPatches = patchInfos.getJSONArray("groups")
                for (i in 0..<groupPatches.length()) {
                    val patchGroupInfo = groupPatches.getJSONObject(i)
                    println(
                        "    • " + getPatchInfoString(
                            patchGroupInfo.getString("name"),
                            patchGroupInfo.getString("shortname")
                        )
                    )

                    for (a in 0..<patchGroupInfo.getJSONArray("infos").length()) {
                        val info = patchGroupInfo.getJSONArray("infos").getJSONObject(a)
                        println("        • " + getPatchInfoString(info.getString("name"), info.getString("shortname")))
                    }
                }
            }

           */
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while loading patch list from core")
            exitProcess(-1)
        }
    }

    fun printGroupPatches(info: JSONArray) {
        info.forEach { patch ->
            patch as JSONObject
            val name = patch.getString("name")
            val shortname = patch.getString("shortname")
            val author = patch.optString("author")
            val desc = patch.optString("desc")

            println("        - $name")
            println("            Shortname: $shortname")
            println("            Description: $desc")
            println("            Tasks:")
            val tasks = patch.getJSONObject("tasks")

            tasks.keys().asSequence()
                .sortedBy { it.toIntOrNull() }
                .forEach { key ->
                    println("                $key. ${tasks.getString(key)}")
                }
        }
    }

    fun printPatch(info: JSONObject) {
        val name = info.getString("name")
        val shortname = info.getString("shortname")
        val author = info.optString("author")
        val desc = info.optString("desc")

        println("    • $name (by $author)")
        println("        Shortname: $shortname")
        println("        Description: $desc")

        val tasks = info.getJSONObject("tasks")
        if (tasks.isEmpty) {
            println("          Tasks: (none)")
        } else {
            println("          Tasks:")
            tasks.keys().asSequence()
                .sortedBy { it.toIntOrNull() }
                .forEach { key ->
                    println("              $key. ${tasks.getString(key)}")
                }
        }
    }


    fun getPatchInfoString(name: String, shortname: String): String = "$name ($shortname)"
}