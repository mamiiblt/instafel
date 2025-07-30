package me.mamiiblt.instafel.updater.utils;

import android.app.Activity;

import com.scottyab.rootbeer.RootBeer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class RootManager {

    public static boolean isDeviceRooted(Activity act) {
        return new RootBeer(act.getApplicationContext()).isRooted();
    }

    public static CommandOutput execSuCommands(String... commands) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                os.writeBytes(command + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            StringBuilder errorOutput = new StringBuilder();
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            process.waitFor();
            return new CommandOutput(process.exitValue(), output.toString(), errorOutput.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new CommandOutput(-1, output.toString(), null);
        }
    }
}
