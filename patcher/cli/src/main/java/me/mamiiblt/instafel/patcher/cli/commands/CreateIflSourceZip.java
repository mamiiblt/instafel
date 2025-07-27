package me.mamiiblt.instafel.patcher.cli.commands;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;

import me.mamiiblt.instafel.patcher.cli.handlers.Command;
import me.mamiiblt.instafel.patcher.cli.handlers.CoreHandler;
import me.mamiiblt.instafel.patcher.cli.utils.Log;
import me.mamiiblt.instafel.patcher.cli.utils.Utils;

public class CreateIflSourceZip implements Command {

    @Override
    public void execute(String[] args) {
        try {
            if (args.length != 0) {
                String fileArgument = args[0];
                if (fileArgument.contains(".apk")) {
                    File apkFile = new File(Paths.get(Utils.USER_DIR, fileArgument).toString());
                    
                    if (!apkFile.exists()) {
                        Log.severe("The specified file does not exist: " + apkFile.getAbsolutePath());
                        return;
                    }

                    Class<?> clazz = CoreHandler.coreClassLoader.loadClass("me.mamiiblt.instafel.patcher.core.jobs.CreateIflZip");
                    Method method = clazz.getMethod("createZip", String.class);

                    Thread task = new Thread(() -> {
                        try {
                            method.invoke(null, apkFile.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    task.start();
                } else {
                    Log.warning("Please select an .apk file");
                }
            } else {
                Log.info("Wrong commage usage type, use like that;");
                Log.info("java -jar patcher.jar init instagram.apk");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.info("An error occured while running command");
            System.exit(-1);
        }

    }
    
}
