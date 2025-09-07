package instafel.patcher.core.providers

import instafel.patcher.core.PatchLoader
import instafel.patcher.core.utils.patch.InstafelPatchGroup
import instafel.patcher.core.utils.patch.PInfos
import instafel.patcher.core.utils.Log
import org.json.JSONArray
import org.json.JSONObject
import kotlin.system.exitProcess

object InfoProvider {
    fun getPatchesList(): JSONObject {
        try {
            val resp = JSONObject()
            val singles = JSONArray()
            val groups = JSONArray()

            val patchInfos = PatchLoader.getPatchInfos()
            patchInfos.filter { it.isSingle }.forEach { pInfo ->
                singles.put(JSONObject().apply {
                    put("shortname", pInfo.shortname)
                    put("name", pInfo.name)
                    put("desc", pInfo.desc)
                    put("author", pInfo.author)
                    put("isSingle", pInfo.isSingle)
                })
            }

            val patchGroupInfos = PatchLoader.getPatchGroupInfos()
            patchGroupInfos.forEach { patchGroupInfo ->
                val group: InstafelPatchGroup? = PatchLoader.findPatchGroupByShortname(patchGroupInfo.shortname)
                group?.let {
                    val groupData = JSONObject().apply {
                        put("shortname", it.shortname)
                        put("name", it.name)
                        put("desc", it.description)
                        put("author", it.author)
                    }

                    val gPatchInfos = JSONArray()
                    it.loadPatches()
                    it.patches.forEach { patchClass ->
                        val info = patchClass.java.getAnnotation(PInfos.PatchInfo::class.java)
                        gPatchInfos.put(JSONObject().apply {
                            put("shortname", info.shortname)
                            put("name", info.name)
                            put("desc", info.desc)
                            put("author", info.author)
                            put("isSingle", info.isSingle)
                        })
                    }
                    groupData.put("infos", gPatchInfos)
                    groups.put(groupData)
                }
            }

            resp.put("total_size", patchInfos.size)
            resp.put("singles", singles)
            resp.put("groups", groups)
            return resp
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while loading patch infos from loader ${e.message}")
            exitProcess(-1)
        }
    }
}