package instafel.app.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import instafel.app.activity.ifl_a_menu;
import instafel.app.InstafelEnv;
import instafel.app.managers.OverridesManager;
import instafel.app.ota.CheckUpdates;
import instafel.app.utils.localization.LocalizationUtils;

import java.util.Locale;

public class InitializeInstafel {
    public static Context ctx;

    public static void setContext(Application application) {
        ctx = application;
        Locale iflLocale = LocalizationUtils.getIflLocale(ctx);
        InstafelEnv.IFL_LANG = iflLocale;
        Log.v("IFL", "InstafelEnv.IFL_LANG is set to " + LocalizationUtils.convertToLangCode(iflLocale));
    }

    public static void triggerCheckUpdates(Activity activity) {
        CheckUpdates.checkUpdates(activity);
    }

    public static void triggerUploadMapping(Activity activity) {
        OverridesManager overridesManager = new OverridesManager(activity);
        if (InstafelAdminUser.isUserLogged(activity) && overridesManager.getMappingFile().exists()) {
            new UploadMapping(activity);
        }
    }

    public static void startInstafel() {
        try {
            Intent intent = new Intent(ctx, ifl_a_menu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
