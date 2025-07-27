package me.mamiiblt.instafel.patcher.cli.handlers;

import java.util.HashMap;
import java.util.Map;

import me.mamiiblt.instafel.patcher.cli.commands.AboutCmd;
import me.mamiiblt.instafel.patcher.cli.commands.BuildCmd;
import me.mamiiblt.instafel.patcher.cli.commands.ClearPatcherCache;
import me.mamiiblt.instafel.patcher.cli.commands.ForceUpdateCore;
import me.mamiiblt.instafel.patcher.cli.commands.CreateIflSourceZip;
import me.mamiiblt.instafel.patcher.cli.commands.HelpCmd;
import me.mamiiblt.instafel.patcher.cli.commands.InitProject;
import me.mamiiblt.instafel.patcher.cli.commands.ListPatches;
import me.mamiiblt.instafel.patcher.cli.commands.RunPatch;
import me.mamiiblt.instafel.patcher.cli.commands.UploadPreview;

public class CommandHandler {
    
    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler(String[] args) {
        registerCommands();
        setupHandler(args);
    }

    private void setupHandler(String[] args) {
        if (args.length == 0) {
            System.out.println("Use `help` command for list all commands.");
        } else {
            String command = args[0];
            String[] commandArgs = new String[args.length - 1];
            System.arraycopy(args, 1, commandArgs, 0, args.length - 1);
            executeCommand(command, commandArgs);
        }
    }

    private void executeCommand(String commandName, String[] args) {
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(args);
        } else {
            System.out.println("Unkown command, use `help` for list all commands.");
        }
    }

    private void registerCommands() {
        commands.put("update-core", new ForceUpdateCore());
        commands.put("clear-cache", new ClearPatcherCache());
        commands.put("help", new HelpCmd());
        commands.put("about", new AboutCmd());
        commands.put("list", new ListPatches());
        commands.put("init", new InitProject());
        commands.put("run", new RunPatch());
        commands.put("build", new BuildCmd());
        commands.put("uprew", new UploadPreview());
        commands.put("csrc", new CreateIflSourceZip());
    }
}