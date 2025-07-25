package me.mamiiblt.instafel.managers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

public class NotificationOtaManager {
    private final Context ctx;
    public static String notification_channel_id = "ifl_ota";
    public static int notification_id = 258;
    private static NotificationManager iflNotificationManager = null;

    public NotificationOtaManager(Context ctx) {
        this.ctx = ctx;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                String channelName = "Instafel Update Notification";
                String description = "This channel is required for Instafel's OTA notification feature.";
                NotificationChannel notificationChannel = new NotificationChannel(
                        notification_channel_id,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationChannel.setDescription(description);
                NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            } catch (Exception e) {
                Toast.makeText(ctx, "Error while creating notification channel", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void sendNotification(Context ctx) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(ctx, NotificationOtaManager.notification_channel_id);
        } else {
            builder = new Notification.Builder(ctx);
        }
        builder
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Downloading IFL Update")
                .setContentText("Waiting for server connection..")
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_DEFAULT);

        if (iflNotificationManager == null) {
            iflNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        iflNotificationManager.notify(notification_id, builder.build());
    }
}


