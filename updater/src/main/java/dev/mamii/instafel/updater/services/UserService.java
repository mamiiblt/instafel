package dev.mamii.instafel.updater.services;

import androidx.annotation.Keep;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import dev.mamii.instafel.updater.IUserService;

public class UserService extends IUserService.Stub {

    @Keep
    public UserService() {
    }

    @Override
    public void destroy() {
        System.exit(0);
    }

    @Override
    public String executeShellCommand(String command) {
        Process process = null;
        StringBuilder output = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec(command, null, null);
            BufferedReader mInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader mError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = mInput.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = mError.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
        }
        catch (Exception ignored) {
        }
        finally {
            if (process != null)
                process.destroy();
        }

        return output.toString();
    }

}