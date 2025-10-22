package me.mamiiblt.instafel.updater.update;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import me.mamiiblt.instafel.updater.utils.AppPreferences;
import me.mamiiblt.instafel.updater.utils.LocalizationUtils;
import me.mamiiblt.instafel.updater.utils.LogUtils;
import me.mamiiblt.instafel.updater.utils.ShizukuInstaller;
import me.mamiiblt.instafel.updater.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateWork extends Worker {

    private static final String CHANNEL_ID = "ifl_updater_channel";
    private static final int NOTIFICATION_ID = 1;
    private AppPreferences appPreferences;
    private Context ctx;
    private String uVersion;
    private LogUtils logUtils;

    public UpdateWork(Context ctx, WorkerParameters params) {
        super(ctx, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        logUtils = new LogUtils(getApplicationContext());
        ctx = getApplicationContext();
        logUtils.w("");
        logUtils.w("Work is running.");

        // Get arch and type from SharedPreferences

        String type;
        String prefType = preferences.getString("checker_type", "non");

        if (prefType.equals("Unclone")) {
            type = "uc";
        } else if (prefType.equals("Clone")) {
            type = "c";
        } else {
            type = null;
            sendError("You selected invalid installation type, work is stopped.", true, null);
        }

        // Set AppPreferences

        appPreferences = new AppPreferences(
                preferences.getBoolean("send_notification", true),
                preferences.getBoolean("use_mobile_data", false),
                preferences.getBoolean("12_hour_rule", false),
                preferences.getBoolean("disable_error_notifications", false),
                preferences.getBoolean("crash_logger", false));

        // Check Network Statues

        boolean mDataAllowStatus;

        try {
            if (isNetworkAvailable(getApplicationContext())) {
                if (isMobileDataConnected(getApplicationContext())) {
                    if (appPreferences.isAllowUseMobileData()) {
                        mDataAllowStatus = true;
                    } else {
                        mDataAllowStatus = false;
                    }
                } else {
                    mDataAllowStatus = true;
                }

                if (mDataAllowStatus) {
                    // Get IG's Version Code

                    String versionName;
                    if (type.equals("uc")) {
                        versionName = Utils.getAppVersionCode(getApplicationContext(), "com.instagram.android");
                    } else if (type.equals("c")) {
                        versionName = Utils.getAppVersionCode(getApplicationContext(), "com.instafel.android");
                    } else {
                        versionName = null;
                    }

                    if (versionName != null) {
                        if (versionName.equals("NOT_INSTALLED")) {
                            sendError(
                                    "IG (Instafel / Instagram) is not installed. Please install from official website",
                                    false, null);
                        } else {
                            logUtils.w("Installed IG version is " + versionName);
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url("https://content.api.instafel.app/content/rels/get/latest")
                                        .build();
                                Response response = client.newCall(request).execute();
                                if (response.isSuccessful()) {

                                    JSONObject res = new JSONObject(response.body().string());
                                    int iflVersion = res.getJSONObject("patcherData").getInt("iflVersion");
                                    String version = res.getJSONObject("patcherData").getString("igVersion");
                                    String published_at = res.getJSONObject("patcherData").getString("buildDate");

                                    if (versionName.equals(version)) {
                                        logUtils.w("Update not needed, app is up-to-date.");
                                        return Result.success();
                                    } else {
                                        logUtils.w("New version found " + version + " (Instafel v" + iflVersion + ")");
                                        if (appPreferences.isAllow12HourMode()) {
                                            String publishTs = published_at;
                                            OffsetDateTime parsedDateTime = OffsetDateTime.parse(publishTs,
                                                    DateTimeFormatter.ISO_DATE_TIME);
                                            OffsetDateTime currentDateTime = OffsetDateTime.now(ZoneOffset.UTC);
                                            Duration duration = Duration.between(parsedDateTime, currentDateTime);

                                            if (duration.toHours() >= 12) {
                                                // allow
                                            } else {
                                                logUtils.w("Duration is " + duration.getSeconds()
                                                        + ", so update stopped.");
                                                return Result.success();
                                            }
                                        }

                                        if (Utils.getMethod(ctx) != 1) {
                                            ShizukuInstaller.ensureUserService(ctx);
                                        }

                                        String b_download_url = null;
                                        if (type.equals("uc")) {
                                            String fName = res.getJSONObject("fileInfos").getJSONObject("unclone").getString("fileName");
                                            b_download_url = "https://github.com/mamiiblt/instafel/releases/download/v" + iflVersion +"/" + fName;
                                        } else {
                                            String fName = res.getJSONObject("fileInfos").getJSONObject("clone").getString("fileName");
                                            b_download_url = "https://github.com/mamiiblt/instafel/releases/download/v" + iflVersion +"/" + fName;
                                        }

                                        if (b_download_url != null) {
                                            try {
                                                uVersion = version;
                                                Intent fgServiceIntent = new Intent(ctx, InstafelUpdateService.class);
                                                fgServiceIntent.putExtra("file_url", b_download_url);
                                                fgServiceIntent.putExtra("version", uVersion);
                                                ctx.startService(fgServiceIntent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                logUtils.w("Error while starting service.");
                                            }
                                        } else {
                                            sendError("Updater can't found update asset!", true, null);
                                        }
                                    }
                                } else {
                                    sendError("Response code is not 200 (" + response.code() + ").", false, null);
                                }
                            } catch (Exception e) {
                                sendError("Error while sending / reading API request", true, e);
                            }
                        }
                    } else {
                        sendError("versionCode is NULL", true, null);
                    }
                } else {
                    logUtils.w("Update couldn't checked because mobile data is enabled.");
                }
            } else {
                logUtils.w("We can't connect to Internet, check failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (appPreferences.isCrashLoggerEnabled()) {
                logUtils.w("ERROR: " + e.getMessage());
                logUtils.w("MSG: " + e.getMessage());
                logUtils.w("CLASS: " + e.getClass().toString());
                logUtils.w("TRACE: " + Log.getStackTraceString(e));
            }
        }

        return Result.success();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            return connectivityManager.getNetworkCapabilities(network) != null;
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    public boolean isMobileDataConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            }
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    private void sendError(String message, boolean priority, Exception e) {
        new Handler(Looper.getMainLooper()).post(() -> {

            // WRITE LOG

            LogUtils logUtils = new LogUtils(getApplicationContext());
            logUtils.w("ERROR: " + message);

            if (e != null) {
                logUtils.w("MSG: " + e.getMessage());
                logUtils.w("CLASS: " + e.getClass().toString());
                logUtils.w("TRACE: " + Log.getStackTraceString(e));
            }

            // SHOW NOTIFICATION OR TOAST

            if (appPreferences.isDisable_error_notifications()) {
                if (priority) {
                    sendErrorNotif(message);
                }
            } else {
                sendErrorNotif(message);
            }
        });
    }

    private void sendErrorNotif(String message) {
        if (appPreferences.isAllowNotification()) {
            NotificationManager notificationManager = (NotificationManager) ctx
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Warning Channel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                    .setContentTitle("Work Error")
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        } else {
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
        }
    }
}
