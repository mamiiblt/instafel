package instafel.patcher.core.utils.patch

import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.SmaliUtils
import kotlin.system.exitProcess

abstract class InstafelPatch {

    var name: String
    var author: String
    var description: String
    var shortname: String
    var isSingle = true
    lateinit var tasks: MutableList<InstafelTask>

    init {
        try {
            val patchInfo = this.javaClass.getAnnotation(PInfos.PatchInfo::class.java)
            if (patchInfo != null) {
                this.name = patchInfo.name
                this.author = patchInfo.author
                this.description = patchInfo.desc
                this.shortname = patchInfo.shortname
                this.isSingle = patchInfo.isSingle
            } else {
                Log.severe("Please add PatchInfo for running patches")
                exitProcess(-1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("Error while constructing InstafelPatch")
            exitProcess(-1)
        }
    }

    val patchInfo: PInfos.PatchInfo?
        get() = this.javaClass.getAnnotation<PInfos.PatchInfo?>(PInfos.PatchInfo::class.java)

    val smaliUtils: SmaliUtils
        get() = SmaliUtils(Env.PROJECT_DIR)

    abstract fun initializeTasks(): MutableList<InstafelTask>

    fun loadTasks() {
        tasks = initializeTasks()
    }
}