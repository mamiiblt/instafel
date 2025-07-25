package me.mamiiblt.instafel.ota.tasks;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URLConnection;
import java.text.DecimalFormat;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.NotificationOtaManager;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.utils.InstafelFileProvider;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;

public class DownloadUpdateTask extends AsyncTask<String, Integer, String> {

    private ProgressBar progressBar;
    private TextView statusText;
    private TextView sizeProcess;
    private InstafelDialog instafelDialog;

    private double currentDownloadedSizeMegabyte;
    private long fileSize;
    private String formattedFileSize;
    private DecimalFormat df;
    private boolean downloadMethod = false;


    Activity activity;
    public DownloadUpdateTask(String method, Activity _activity, ProgressBar progressBar, TextView _statusText, InstafelDialog _dialog, TextView _sizeProcess) {
        this.activity = _activity;
        this.progressBar = progressBar;
        this.statusText = _statusText;
        this.sizeProcess = _sizeProcess;
        this.instafelDialog = _dialog;
        this.df = new DecimalFormat("#.##");

        if (method.equals("NEW")) {
            this.downloadMethod = true;
        } else {
            this.downloadMethod = false;
        }
    }

    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private int notificationId = 258;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (downloadMethod) {
            notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder = new Notification.Builder(activity, NotificationOtaManager.notification_channel_id)
                    .setContentTitle("Instafel Update")
                    .setContentText("Waiting server connection..")
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_DEFAULT);

            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            File f = new File(activity.getExternalFilesDir(null), "instafel_files");
            if (!f.exists()) {
                f.mkdirs();
            } else {
                f.delete();
                f.mkdirs();
            }

            File ifl_update_file = new File(f.getPath(), "ifl_update.apk");
            try {
                URL url = new URL(f_url[0]);
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
                    currentDownloadedSizeMegabyte = (double) total / (1024 * 1024);;
                    publishProgress((int) ((total * 100) / fileSize));
                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    private static final int PROGRESS_UPDATE_INTERVAL = 1200; // 1.2 saniye
    private long lastUpdateTime = 0;
    protected void onProgressUpdate(Integer... progress) {
        int prog = progress[0];
        String formattedDownloadedSize = df.format(currentDownloadedSizeMegabyte);
        if (formattedDownloadedSize.contains(".")) {
            String[] parts = formattedDownloadedSize.split("\\.");
            if (parts.length > 1 && parts[1].length() == 1) {
                formattedDownloadedSize += "0";
            }
        }

        if (!downloadMethod) {
            progressBar.setProgress(prog);
            statusText.setText(prog + "%");
            sizeProcess.setText(formattedDownloadedSize + "/" + formattedFileSize + " MB");
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime >= PROGRESS_UPDATE_INTERVAL || prog == 100) {
                lastUpdateTime = currentTime;

                notificationBuilder
                        .setContentText(prog + "% downloaded (" + formattedDownloadedSize + " MB)")
                        .setProgress(100, prog, false);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        }
    }
    
    @Override
    protected void onPostExecute(String file_url) {
        File f = new File(activity.getExternalFilesDir(null), "instafel_files");
        File ifl_update_file = new File(f.getPath(), "ifl_update.apk");

        if (ifl_update_file.length() == fileSize) {
           if (ifl_update_file.exists()) {
               Intent intent = new Intent(Intent.ACTION_VIEW);
               intent.setDataAndType(InstafelFileProvider.getFileUri(ifl_update_file.getName()), "application/vnd.android.package-archive");
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

               try {
                   if (!downloadMethod) {
                       instafelDialog.dismiss();
                       activity.startActivity(intent);

                       PreferenceManager preferenceManager = new PreferenceManager(activity);
                       preferenceManager.setPreferenceLong(PreferenceKeys.ifl_ota_last_success_install, System.currentTimeMillis());
                   } else {
                       PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                       notificationBuilder
                               .setContentText("Download complete, tap to install.")
                               .setSmallIcon(android.R.drawable.stat_sys_download_done)
                               .setContentIntent(pendingIntent)
                               .setProgress(0, 0, false);
                       notificationManager.notify(notificationId, notificationBuilder.build());
                   }

               } catch (Exception e) {
                   e.printStackTrace();
                   if (!downloadMethod) {
                       Toast.makeText(activity, activity.getString(R.string.ifl_t1_01) ,Toast.LENGTH_LONG).show();
                   } else {
                       notificationBuilder
                               .setContentText("ERR1")
                               .setSmallIcon(android.R.drawable.stat_notify_error)
                               .setProgress(0, 0, false);
                       notificationManager.notify(notificationId, notificationBuilder.build());
                   }
               }
           } else {
               if (!downloadMethod) {
                   instafelDialog.dismiss();
                   Toast.makeText(activity, activity.getString(R.string.ifl_t1_03), Toast.LENGTH_LONG).show();
               } else {
                   notificationBuilder
                           .setContentText("ERR2")
                           .setSmallIcon(android.R.drawable.stat_notify_error)
                           .setProgress(0, 0, false);
                   notificationManager.notify(notificationId, notificationBuilder.build());
               }
           }
       } else {
            if (!downloadMethod) {
                instafelDialog.dismiss();
                Toast.makeText(activity, activity.getString(R.string.ifl_t1_03), Toast.LENGTH_LONG).show();
            } else {
                notificationBuilder
                        .setContentText("ERR3")
                        .setSmallIcon(android.R.drawable.stat_notify_error)
                        .setProgress(0, 0, false);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
       }
    }
}
