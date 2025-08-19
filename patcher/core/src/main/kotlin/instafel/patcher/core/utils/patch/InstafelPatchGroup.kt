package instafel.patcher.core.utils.patch

import instafel.patcher.core.utils.Log
import kotlin.reflect.KClass

abstract class InstafelPatchGroup {

    var name: String
    var author: String
    var description: String
    var shortname: String
    var patches: List<KClass<out InstafelPatch>> = emptyList()

    init {
        try {
            val patchInfo = this::class.java.getAnnotation(PInfos.PatchGroupInfo::class.java)
            if (patchInfo != null) {
                name = patchInfo.name
                author = patchInfo.author
                description = patchInfo.desc
                shortname = patchInfo.shortname
            } else {
                Log.severe("Please add PatchGroupInfo attr for creating patch group")
                kotlin.system.exitProcess(-1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while constructing InstafelPatchGroup")
            kotlin.system.exitProcess(-1)
        }
    }

    @Throws(Exception::class)
    abstract fun initializePatches(): List<KClass<out InstafelPatch>>

    @Throws(Exception::class)
    fun loadPatches() {
        patches = initializePatches()
    }
}
