package dev.mamii.instafel.updater.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {

    private Context ctx;
    private final File logFile;
    private final String LOG_FILE_NAME = "app_log.txt";

    public LogUtils(Context ctx) {
        this.ctx = ctx;
        this.logFile = new File(ctx.getFilesDir(), LOG_FILE_NAME);
    }

    public void w(String message) {

        checkAndWriteHeader();

        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            fos.write((getTs() + " : " + message + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLog() {
        StringBuilder logContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logContent.toString();
    }

    private void checkAndWriteHeader() {
        String existingDate = readDateFromLogFile();

        if (existingDate == null || !existingDate.equals(getCurrentDay())) {
            clearLog();
        }
    }

    public void clearLog() {
        try (FileOutputStream fos = new FileOutputStream(logFile, false)) {
            fos.write(("Instafel Updater Log" + "\n").getBytes());
            fos.write((getCurrentDay() + "\n\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readDateFromLogFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            reader.readLine();
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTs() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private String getCurrentDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
        return dateFormat.format(new Date());
    }

}
