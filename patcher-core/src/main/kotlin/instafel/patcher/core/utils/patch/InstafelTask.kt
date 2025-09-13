package instafel.patcher.core.utils.patch

import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import kotlin.system.exitProcess

abstract class InstafelTask() {

    private var taskStatus: Int = 0
    private lateinit var finishString: String
    var taskName: String

    init {
        try {
            val taskInfo = this.javaClass.getAnnotation(PInfos.TaskInfo::class.java)
            if (taskInfo != null) {
                this.taskName = taskInfo.name
            } else {
                Log.severe("Please add TaskInfo for running tasks")
                exitProcess(-1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("Error while constructing InstafelTask")
            exitProcess(-1)
        }
    }

    @Throws(Exception::class)
    abstract fun execute()

    fun success(message: String) {
        taskStatus = 1
        finishString = message
        finishTask()
    }

    fun failure(message: String) {
        taskStatus = 2
        finishString = message
        finishTask()
    }

    private fun finishTask() {
        if (this::finishString.isInitialized) {
            when (taskStatus) {
                1 -> Log.info("SUCCESS: $finishString")
                2 -> {
                    Log.info("FAILURE: $finishString")
                    Log.info(Env.SEPARATOR_LINE)
                    exitProcess(-1)
                }
            }
        } else {
            Log.severe("This task not finished yet, wait until it finished by runner")
            exitProcess(-1)
        }
    }
}
