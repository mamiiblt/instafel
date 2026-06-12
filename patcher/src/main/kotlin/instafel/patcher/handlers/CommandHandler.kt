/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.handlers

import instafel.patcher.commands.*

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
        commands["run"] = RunPatch()
        commands["build"] = BuildCmd()
        commands["csrc"] = CreateIflSourceZip()
        commands["uprew"] = UploadPreview()
        commands["update-core"] = ForceUpdateCore()
        commands["clear-cache"] = ClearPatcherCache()
    }
}