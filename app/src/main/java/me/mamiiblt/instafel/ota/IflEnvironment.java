package me.mamiiblt.instafel.ota;

import android.app.Activity;
import android.content.Context;

import java.util.Locale;

import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.utils.localization.LocalizedStringGetter;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;

public class IflEnvironment {

    public static int getIflVersion(Context ctx) {
        PreferenceManager preferenceManager = new PreferenceManager(ctx);
        int customValue = preferenceManager.getPreferenceInt(PreferenceKeys.ifl_custom_ifl_version, 0);
        if (customValue == 0) {
            String rawVersion = InstafelEnv.IFL_VERSION;
            try {
                return Integer.parseInt(rawVersion);
            } catch (Exception e) {
                return 0;
            }
        } else {
            return customValue;
        }
    }

    public static String getIflVersionString(Activity activity) {
        if (InstafelEnv.PRODUCTION_MODE) {
            return "Release v" + getIflVersion(activity);
        } else {
            return "Custom Generation";
        }
    }

    public static String getIgVerAndCodeString(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        String customIgVer = preferenceManager.getPreferenceString(PreferenceKeys.ifl_custom_ig_version, "");
        String customIgVerCode = preferenceManager.getPreferenceString(PreferenceKeys.ifl_custom_ig_ver_code, "");

        if (!customIgVer.isEmpty()) {
            if (!customIgVerCode.isEmpty()) {
                return "v" + customIgVer + " (" + customIgVerCode + ")";
            } else {
                return "v" + customIgVer + " (" + InstafelEnv.IG_VERSION_CODE + ")";
            }
        } else {
            if (!customIgVerCode.isEmpty()) {
                return "v" + InstafelEnv.IG_VERSION + " (" + customIgVerCode + ")";
            } else {
                return "v" + InstafelEnv.IG_VERSION + " (" + InstafelEnv.IG_VERSION_CODE + ")";
            }
        }
    }

    public static String getIgVerCode(Context ctx) {
        PreferenceManager preferenceManager = new PreferenceManager(ctx);
        String customIgVerCode = preferenceManager.getPreferenceString(PreferenceKeys.ifl_custom_ig_ver_code, "");
        if (customIgVerCode.isEmpty()) {
            return InstafelEnv.IG_VERSION_CODE;
        } else {
            return customIgVerCode;
        }
    }

    public static String getGenerationId(Context ctx) {
        if (InstafelEnv.PRODUCTION_MODE) {
            PreferenceManager preferenceManager = new PreferenceManager(ctx);
            String customGenId = preferenceManager.getPreferenceString(PreferenceKeys.ifl_custom_gen_id, "");
            if (customGenId.isEmpty()) {
                return InstafelEnv.GENERATION_ID;
            } else {
                return customGenId;
            }
        } else {
            return "Custom Generation";
        }
    }

    public static String getIgVersion(Context ctx) {
        PreferenceManager preferenceManager = new PreferenceManager(ctx);
        String customIgVer = preferenceManager.getPreferenceString(PreferenceKeys.ifl_custom_ig_version,"");
        if (customIgVer.isEmpty()) {
            return InstafelEnv.IG_VERSION;
        } else {
            return customIgVer;
        }
    }

    public static String getTypeString(Activity activity, Locale locale) {
        if (getType(activity).equals("Clone")) return LocalizedStringGetter.getLocalizedString(activity, locale.getLanguage(), "ifl_a1_07");
        if (getType(activity).equals("Unclone")) return LocalizedStringGetter.getLocalizedString(activity, locale.getLanguage(), "ifl_a1_08");
        return getType(activity);
    }

    public static String getType(Context ctx) {
        try {
            String packageName = ctx.getApplicationContext().getPackageName();
            if (packageName.equals("com.instagram.android") || packageName.equals("me.mamiiblt.instafel")) {
                return "Unclone";
            } else {
                return "Clone";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Unclone";
        }
    }

    public static String getType(Activity activity) {
        String packageName = activity.getApplicationContext().getPackageName();
        if (packageName.equals("com.instagram.android") || packageName.equals("me.mamiiblt.instafel")) {
            return "Unclone";
        } else {
            return "Clone";
        }
    }
}
