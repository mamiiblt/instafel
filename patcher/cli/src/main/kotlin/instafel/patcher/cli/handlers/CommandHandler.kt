package instafel.patcher.cli.handlers

import instafel.patcher.cli.commands.AboutCmd
import instafel.patcher.cli.commands.HelpCmd
import instafel.patcher.cli.commands.InitProject
import instafel.patcher.cli.commands.ListPatches

class CommandHandler(val args: Array<String>) {
    var commands: MutableMap<String, Command> = HashMap();

    init {
        registerCommands();
        setupHandler();
    }

    fun setupHandler() {
        if (args.count() == 0) {
            println("Use `help` command for list all commands")
        } else {
            val command = args.first()
            val commandArgs = args.drop(1).toTypedArray()
            executeCommand(command, commandArgs);
        }
    }

    private fun executeCommand(commandName: String, args: Array<String>) {
        val command = commands[commandName]
        if (command != null) {
            command.execute(args)
        } else {
            println("Unknown command, use `help` to list all commands.")
        }
    }

    private fun registerCommands() {
        commands["help"] = HelpCmd();
        commands["about"] = AboutCmd()
        commands["list"] = ListPatches()
        commands["init"] = InitProject()
        /*commands["update-core"] = ForceUpdateCore()
        commands["clear-cache"] = ClearPatcherCache()
        commands["run"] = RunPatch()
        commands["build"] = BuildCmd()
        commands["uprew"] = UploadPreview()
        commands["csrc"] = CreateIflSourceZip()*/
    }
}