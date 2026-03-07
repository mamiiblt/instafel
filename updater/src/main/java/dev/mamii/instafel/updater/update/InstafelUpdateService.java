package dev.mamii.instafel.updater.update;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import dev.mamii.instafel.updater.R;
import dev.mamii.instafel.updater.utils.AppPreferences;
import dev.mamii.instafel.updater.utils.CommandOutput;
import dev.mamii.instafel.updater.utils.LogUtils;
import dev.mamii.instafel.updater.utils.RootManager;
import dev.mamii.instafel.updater.utils.ShizukuInstaller;
import dev.mamii.instafel.updater.utils.Utils;

public class InstafelUpdateService extends Service {

    private static String CHANNEL_ID = "ifl_updater_channel";
    private static int NOTIFICATION_ID = 1;
    private double currentDownloadedSizeMegabyte;
    private long fileSize;
    private String formattedFileSize;
    private DecimalFormat df;
    private Context ctx;
    private LogUtils logUtils;
    private String version;
    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    SharedPreferences preferences = null;
    private AppPreferences appPreferences;

    @Override
    public void onCreate() {
        this.df = new DecimalFormat("#.##");
        this.ctx = getApplicationContext();
        this.logUtils = new LogUtils(ctx);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        this.notificationManager = ctx.getSystemService(NotificationManager.class);
        this.notificationBuilder = new NotificationCompat.Builder(ctx, CHANNEL_ID);
        this.appPreferences = new AppPreferences(
                preferences.getBoolean("send_notification", true),
                preferences.getBoolean("use_mobile_data", false),
                preferences.getBoolean("12_hour_rule", false),
                preferences.getBoolean("disable_error_notifications", false),
                preferences.getBoolean("crash_logger", false));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("file_url") && intent.hasExtra("version")) {
            createNotificationChannel();
            startForeground(NOTIFICATION_ID, notifyWaitingAPI());
            version = intent.getStringExtra("version");
            downloadFile(intent.getStringExtra("file_url"));
        }
        return START_NOT_STICKY;
    }

    private void downloadFile(String fileUrl) {
        if (Utils.getMethod(ctx) != 1) {
            ShizukuInstaller.ensureUserService(ctx);
        }
        this.df = new DecimalFormat("#.##");

        try {
            File f = recreateFilesDir();

            File ifl_update_file = new File(f.getPath(), "update.apk");
            new Thread(() -> {
                logUtils.w("Downloading update.. ");
                int count;
                boolean errorOccured = false;
                try {
                    URL url = new URL(fileUrl);
                    URLConnection connection = url.openConnection();
                    connection.connect();

                    long fileOfSize = connection.getContentLength();
                    fileSize = fileOfSize;
                    formattedFileSize = df.format((double) fileOfSize / (1024 * 1024));
                    if (formattedFileSize.contains(".")) {
                        String[] parts = formattedFileSize.split("\\.");
                        if (parts.length > 1 && parts[1].length() == 1) {
                            formattedFileSize += "0";
                        }
                    }

                    InputStream input = new BufferedInputStream(url.openStream(),
                            8192);

                    OutputStream output = new FileOutputStream(ifl_update_file.getPath());

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                        currentDownloadedSizeMegabyte = (double) total / (1024 * 1024);
                        updateStatus((int) ((total * 100) / fileSize));
                    }

                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (appPreferences.isCrashLoggerEnabled()) {
                        logUtils.w("Error while downloading update (IO). [" + e.getMessage() + "]");
                        logUtils.w("MSG: " + e.getMessage());
                        logUtils.w("CLASS: " + e.getClass().toString());
                        logUtils.w("TRACE: " + Log.getStackTraceString(e));
                    } else {
                        logUtils.w("An error occurred, " + e.getMessage());
                    }
                    errorOccured = true;
                } finally {
                    if (!errorOccured) {
                        if (Utils.getMethod(ctx) != 1) {
                            ShizukuInstaller.ensureUserService(ctx);
                        }
                        updateApp(ifl_update_file);
                    }
                }
            }).start();

        } catch (Exception e) {

            if (appPreferences.isCrashLoggerEnabled()) {
                logUtils.w("Error while ensuring connection.");
                logUtils.w("MSG: " + e.getMessage());
                logUtils.w("CLASS: " + e.getClass().toString());
                logUtils.w("TRACE: " + Log.getStackTraceString(e));
            } else {
                logUtils.w("An error occurred, " + e.getMessage());
            }
            notifyError( "An error occured when downloading update");
            e.printStackTrace();
        }
    }

    private File recreateFilesDir() {
        File f = new File(ctx.getExternalFilesDir(null), "downloaded_apks");
        if (!f.exists()) {
            f.mkdirs();
        } else {
            f.delete();
            f.mkdirs();
        }
        return f;
    }

    private static final int PROGRESS_UPDATE_INTERVAL = 1200;
    private long lastUpdateTime = 0;
    private void updateStatus(int prog) {
        String formattedDownloadedSize = df.format(currentDownloadedSizeMegabyte);
        if (formattedDownloadedSize.contains(".")) {
            String[] parts = formattedDownloadedSize.split("\\.");
            if (parts.length > 1 && parts[1].length() == 1) {
                formattedDownloadedSize += "0";
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= PROGRESS_UPDATE_INTERVAL) {
            lastUpdateTime = currentTime;
            notifyStatus(prog, formattedDownloadedSize);
        }
    }

    private void updateApp(File ifl_update_file) {
        notifyInstallingUpdate();
        logUtils.w("Download complete, installation is started.");
        new Thread(() -> {
            try {
                if (Utils.getMethod(ctx) == 1) {
                    installApk(ifl_update_file, true);
                } else {
                    if (ShizukuInstaller.isShizukuSupported()) {
                        if (Utils.hasShizukuPermission()) {
                            for (int i = 0; i < 5; i++) {
                                if (ShizukuInstaller.ensureUserService(ctx)) {
                                    i = 10;
                                } else {
                                    i++;
                                }
                            }
                            installApk(ifl_update_file, false);
                        } else {
                            logUtils.w("Shizuku permission is not granted for Install.");
                        }
                    } else {
                        logUtils.w("Shizuku is not supported.");
                    }
                }
            } catch (Exception e) {
                logUtils.w("Installation method crashed.");
                logUtils.w("MSG: " + e.getMessage());
                logUtils.w("CLASS: " + e.getClass().toString());
                logUtils.w("TRACE: " + Log.getStackTraceString(e));
                notifyError("An error occurred when installing update (CRASH)");
            }
        }).start();
    }

    private void installApk(File ifl_update_file, boolean status) {
        if (status) {
            logUtils.w("Installing update");
            CommandOutput commandOutput = RootManager.execSuCommands(
                    "cp " + ifl_update_file.getAbsolutePath() + " /data/local/tmp/INSTAFEL_UPDATE.apk",
                    "pm install /data/local/tmp/INSTAFEL_UPDATE.apk",
                    "rm /data/local/tmp/INSTAFEL_UPDATE.apk"
            );

            if (commandOutput.getExitCode() == 0 && commandOutput.getLog().contains("Success")) {
                logUtils.w("Update installed.");
                recreateFilesDir();
                notifyUpdateInstalled();
                logUtils.w("Instafel succesfully updated.");
            } else {
                logUtils.w("Update installation failed.");
                notifyError("An error occurred when installing update (RESULT_FAILED)");
                logUtils.w("exitCode: " + commandOutput.getExitCode());
                logUtils.w("output: " + commandOutput.getLog());
                logUtils.w("errOutput: " + commandOutput.getErrorLog());
            }
        } else {
            logUtils.w("Shizuku permission is valid, triggering UserService again, " + ShizukuInstaller.ensureUserService(ctx));
            logUtils.w("Copying downloaded apk file to temp.");
            ShizukuInstaller.runCommand(ctx, "cp " + ifl_update_file + " /data/local/tmp/INSTAFEL_UPDATE.apk");
            logUtils.w("Installing update");
            String updateLog = ShizukuInstaller.runCommand(ctx, "pm install /data/local/tmp/INSTAFEL_UPDATE.apk");
            if (updateLog.trim().equals("Success")) {
                logUtils.w("Update installed.");
                ShizukuInstaller.runCommand(ctx, "rm -r /data/local/tmp/INSTAFEL_UPDATE.apk");
                recreateFilesDir();
                logUtils.w("Downloaded apk & temp apk removed");
                notifyUpdateInstalled();
                logUtils.w("Instafel succesfully updated.");
            } else {
                logUtils.w("Update installation failed.");
                notifyError("An error occurred when installing update (RESULT_FAILED)");
            }
        }
    }

    private void notifyStatus(int prog, String finalFormattedDownloadedSize) {
        notificationBuilder
                .setContentTitle(ctx.getString(R.string.n1_downloading))
                .setContentText(prog + "% (" + finalFormattedDownloadedSize + " / " + formattedFileSize +" MB)")
                .setSound(null)
                .setDefaults(0)
                .setProgress(100, prog, false);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    private Notification notifyWaitingAPI() {
        notificationBuilder
                .setContentTitle(ctx.getString(R.string.n2_waiting))
                .setContentText(ctx.getString(R.string.n2_sub))
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        return notificationBuilder.build();
    }

    private void notifyInstallingUpdate() {
        notificationBuilder
                .setContentTitle(ctx.getString(R.string.n3_installing))
                .setContentText(ctx.getString(R.string.n3_sub))
                .setProgress(0, 0, true)
                .setSmallIcon(R.drawable.installing);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private void notifyUpdateInstalled() {
       if (appPreferences.isAllowNotification()) {
           notificationBuilder
                   .setContentTitle(ctx.getString(R.string.n4_complete))
                   .setContentText(ctx.getString(R.string.n4_sub, version))
                   .setSmallIcon(R.drawable.update_success)
                   .setProgress(0, 0, false);
           notificationManager.notify(1, notificationBuilder.build());
       } else {
           notificationManager.cancel(1);
       }
    }

    private void notifyError(String error) {
        if (appPreferences.isDisable_error_notifications() == false) {
            notificationBuilder
                    .setContentTitle(ctx.getString(R.string.error))
                    .setContentText(error)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setProgress(0, 0, false);
            notificationManager.notify(1, notificationBuilder.build());
        } else {
            notificationManager.cancel(1);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Update Download Service Notif.",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
