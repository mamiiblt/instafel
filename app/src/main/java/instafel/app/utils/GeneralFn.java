package instafel.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import instafel.app.R;
import instafel.app.InstafelEnv;
import instafel.app.managers.PreferenceManager;
import instafel.app.utils.types.PreferenceKeys;

public class GeneralFn {

    public static String DEFAULT_API_PATH = "https://api.mamii.me/ifl";
    public static String DEFAULT_CONTENT_API_PATH = "https://content.api.instafel.app";

    public static String getApiUrl(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        boolean debugModeStatus = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_enable_debug_mode, false);
        if (debugModeStatus) {
            return preferenceManager.getPreferenceString(PreferenceKeys.ifl_debug_api_url, "DEBUG_URL");
        } else {
            return DEFAULT_API_PATH;
        }
    }

    public static String getContentApiUrl(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        boolean debugModeStatus = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_enable_debug_mode, false);
        if (debugModeStatus) {
            return preferenceManager.getPreferenceString(PreferenceKeys.ifl_debug_content_api_url, "DEBUG_URL");
        } else {
            return DEFAULT_CONTENT_API_PATH;
        }
    }

    public static String encodeString(String string) {
        return Base64.encodeToString(string.getBytes(), Base64.NO_WRAP);
    }

    public static String decodeString(String string) {
        return new String(java.util.Base64.getDecoder().decode(string), StandardCharsets.UTF_8);
    }

    public static String getFileHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        InputStream fis = Files.newInputStream(file.toPath());
        byte[] buffer = new byte[8192];
        int n;
        while ((n = fis.read(buffer)) > 0) {
            digest.update(buffer, 0, n);
        }
        fis.close();
        byte[] hashBytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static int convertToDp(Context ctx, int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val,
                ctx.getResources().getDisplayMetrics());
    }

    public static Resources getAppResources(Activity activity) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            Resources res = packageManager.getResourcesForApplication(getPackageName(activity));
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public static Resources getAppResourcesWithConf(Activity activity, String langCode) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            Resources res = packageManager.getResourcesForApplication(getPackageName(activity));
            final Configuration config = new Configuration();
            config.locale = new Locale(langCode);
            res.updateConfiguration(config, null);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getStringResId(Activity activity, Resources appResources, String stringName) {
        return appResources.getIdentifier(stringName, "string", getPackageName(activity));
    }

    public static String getPackageName(Activity activity) {
        return activity.getPackageName();
    }

    public static void startIntentWithString(Activity activity, Class newIntent, String data) {
        try {
            Intent aboutIntent = new Intent(activity, newIntent);
            aboutIntent.putExtra("data", data);
            aboutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(aboutIntent);
        } catch (Exception e) {
            Toast.makeText(activity, "failed_start_intent", Toast.LENGTH_SHORT).show();
        }
    }

    public static void startIntent(Activity activity, Class newIntent) {
        try {
            Intent aboutIntent = new Intent(activity, newIntent);
            aboutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(aboutIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void openInWebBrowser(Context ctx, String url) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ctx, ctx.getString(R.string.ifl_c1_01), Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateIflUi(ComponentActivity activity) {
        try {
            if (InstafelEnv.IFL_THEME == 25891 || InstafelEnv.IFL_THEME == 3) {
                InstafelEnv.IFL_THEME = getUiMode(activity);
            }
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTheme(activity, window);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTheme(ComponentActivity activity, Window window) {
        if (InstafelEnv.IFL_THEME == 0 || InstafelEnv.IFL_THEME == 1 || InstafelEnv.IFL_THEME == 3
                || InstafelEnv.IFL_THEME == 25891) {
            activity.setTheme(R.style.ifl_theme_dark);
            window.setStatusBarColor(activity.getResources().getColor(R.color.ifl_background_color));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.ifl_background_color));
        } else {
            if (InstafelEnv.IFL_THEME == 2) {
                activity.setTheme(R.style.ifl_theme_light);
                window.setStatusBarColor(activity.getResources().getColor(R.color.ifl_background_color_light));
                window.setNavigationBarColor(activity.getResources().getColor(R.color.ifl_background_color_light));
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                return;
            }
            activity.setTheme(R.style.ifl_theme_dark);
            window.setStatusBarColor(activity.getResources().getColor(R.color.ifl_background_color));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.ifl_background_color));
        }
    }

    /*
     * 0 = error / dark
     * 1 = dark
     * 2 = light
     */
    public static int getUiMode(Activity activity) {
        try {
            int nightModeFlags = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                return 1;
            } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                return 2;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
