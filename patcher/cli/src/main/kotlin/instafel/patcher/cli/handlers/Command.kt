package instafel.patcher.cli.handlers

interface Command {
    fun execute(args: Array<String>)
}