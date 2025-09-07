package instafel.patcher.handlers

interface Command {
    fun execute(args: Array<String>)
}