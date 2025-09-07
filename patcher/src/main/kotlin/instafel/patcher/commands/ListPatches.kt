package instafel.patcher.commands

import instafel.patcher.handlers.Command
import instafel.patcher.handlers.CoreHandler
import instafel.patcher.utils.Log
import org.json.JSONObject
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

            val patchInfos = CoreHandler.invokeKotlinObject(
                "providers.InfoProvider",
                "getPatchesList"
            ) as JSONObject

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

            println("")
            println("Use run <wdir> name... for executing patches / patch groups")
            println("Totally found ${patchInfos.getInt("total_size")} patch.")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while loading patch list from core")
            exitProcess(-1)
        }
    }

    fun getPatchInfoString(name: String, shortname: String): String = "$name ($shortname)"
}