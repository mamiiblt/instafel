package instafel.patcher.core

import com.google.gson.Gson
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.modals.PatchInfo
import instafel.patcher.core.utils.modals.PatchesInfo
import instafel.patcher.core.utils.patch.InstafelPatch
import kotlin.jvm.java

object PatchInfoLoader {
    private val gson = Gson()
    val patchesInfo: PatchesInfo by lazy { loadPatchInfo() }

    fun loadPatchClass(patchInfo: PatchInfo): InstafelPatch {
        val clazz = Class.forName("${patchesInfo.corePackage}.${patchInfo.path}")
        val instance = clazz.getDeclaredConstructor().newInstance()
        return instance as InstafelPatch
    }

    fun loadPatches(enteredShortnames: Array<*>): Map<PatchInfo, InstafelPatch> {
        val runnablePatches = mutableMapOf<PatchInfo, InstafelPatch>()

        enteredShortnames.forEach { logShortname ->
            patchesInfo.singles.forEach { patch ->
                if (patch.shortname == logShortname) {
                    runnablePatches[patch] = loadPatchClass(patch)
                    Log.info("Patch \'${patch.name}\' loaded")
                }
            }

            patchesInfo.groups.forEach { group ->
                if (group.shortname == logShortname) {
                    group.patches.forEach { patch ->
                        runnablePatches[patch] = loadPatchClass(patch)
                        Log.info("Patch \'${patch.name}\' loaded from \'${group.shortname}\'")
                    }
                    Log.info("All patches in group \'${group.name}\' loaded")
                }
            }
        }

        Log.info("All patches loaded, totally ${runnablePatches.size} patch loaded successfully")
        return runnablePatches
    }

    private fun loadPatchInfo(): PatchesInfo {
        val stream = PatchInfoLoader::class.java.getResourceAsStream("/patches.json")
            ?: throw IllegalArgumentException("patches.json not found in classpath")

        val jsonStr = stream.bufferedReader().readText()
        val pInfo = gson.fromJson(jsonStr, PatchesInfo::class.java)
        return pInfo
    }
}