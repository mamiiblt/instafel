package me.mamiiblt.instafel.updater.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import me.mamiiblt.instafel.updater.BuildConfig;
import me.mamiiblt.instafel.updater.R;
import rikka.shizuku.Shizuku;

public class Utils {

    public static boolean status = false;

    public static boolean hasShizukuPermission() {
        if (Shizuku.isPreV11()) {
            return false;
        }

        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isShizukuInstalled(Activity activity) {
        if (getAppVersionCode(activity, "moe.shizuku.privileged.api").equals("NOT_INSTALLED")) {
            return false;
        } else {
            return true;
        }
    }

    public static void openShizuku(Context ctx) {
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
        ctx.startActivity(intent);
    }

    public static void showBatteryDialog(Context ctx) {
        if (Utils.getBatteryRestrictionStatus(ctx)) {
            new MaterialAlertDialogBuilder(ctx)
                    .setTitle(ctx.getString(R.string.battery_dialog_title))
                    .setMessage(ctx.getString(R.string.battery_dialog_msg))
                    .setPositiveButton(ctx.getString(R.string.dialog_ok), (dialog, which) -> openBatterySettings(ctx))
                    .setCancelable(false)
                    .show();
        }
    }

    public static int getMethod(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (sharedPreferences.getString("checker_method", "shizuku").equals("root")) {
            return 1;
        } else {
            return 2;
        }
    }

    public static void showAppInfoDialog(Context ctx) {
        new MaterialAlertDialogBuilder(ctx)
                .setTitle(ctx.getString(R.string.about_app))
                .setMessage("version: v" + BuildConfig.IFLU_VERSION +
                        "\nchannel: " + BuildConfig.BUILD_TYPE +
                        "\nbase commit: " + BuildConfig.COMMIT + "@" + BuildConfig.BRANCH +
                        "\n\n" + ctx.getString(R.string.developed_by)
                )
                .setNegativeButton("View Commit", (dialog, which) -> openInBrowser(ctx, Uri.parse("https://github.com/mamiiblt/instafel/commit/" + BuildConfig.COMMIT)))
                .setPositiveButton(ctx.getString(R.string.dialog_ok), (dialog, which) -> openBatterySettings(ctx))
                .show();
    }

    public static void openInBrowser(Context ctx, Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    private static void openBatterySettings(Context ctx) {
        String packageName = ctx.getPackageName();
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + packageName));
        ctx.startActivity(intent);
    }

    public static boolean getBatteryRestrictionStatus(Context ctx) {
        PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        String packageName = ctx.getPackageName();

        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            try {
                return true;
            } catch (ActivityNotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static String getAppVersionCode(Context ctx, String packageName) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "NOT_INSTALLED";
        }
    }

    public static void openPlayStore(Context ctx) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static void showDialog(Context ctx, String title, String message) {
        new MaterialAlertDialogBuilder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
