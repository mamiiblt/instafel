package instafel.patcher.core.utils.modals

interface CLIJob {
    fun runJob(vararg args: Any): Any?
}