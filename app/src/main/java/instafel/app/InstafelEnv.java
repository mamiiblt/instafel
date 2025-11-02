package instafel.app;

import android.os.LocaleList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

public class InstafelEnv {
    public static Locale IFL_LANG = null;
    public static int IFL_THEME = 25891;

    // These fields need to be set from patcher.
    public static boolean PRODUCTION_MODE = true;
    public static String IFL_VERSION = "_iflver_"; // 454
    public static String GENERATION_ID = "_genid_"; // 01926031325
    public static String IG_VERSION = "_igver_"; // 371.0.0.0.23
    public static String IG_VERSION_CODE = "_igvercode_"; // 377506971
    public static String PATCHER_VERSION = "_pversion_"; // v1.0.3
    public static String PATCHER_TAG = "_ptag"; // release or debug
    public static String COMMIT = "_commit_"; // 3ed4c6e
    public static String BRANCH = "_branch_"; // main
    public static String APPLIED_PATCHES = "_patchesjson_"; // A escaped JSONObject string

    // This method fields need to be set from translation merge task.
    public static LocaleList getSupportedLocaleList() {
        String[] supportedLanguages = { "en-US", "tr-TR", "hi-IN", "ar-SA", "de-DE", "zh-TW", "in-ID", "zh-HK", "it-IT", "sr-CS", "az-AZ", "es-ES", "zh-CN", "th-TH", "hu-HU", "pl-PL", "fr-FR", "pt-BR", "el-GR" };
        Locale[] locales = new Locale[supportedLanguages.length];

        for (int i = 0; i < supportedLanguages.length; i++) {
            String code = supportedLanguages[i];
            String language;
            String country;

            String[] parts = code.split("-");
            language = parts[0];
            country = parts[1];

            locales[i] = new Locale(language, country);
        }

        return new LocaleList(locales);
    }

    public static boolean isPatchApplied(String patchName) {
        try {
            JSONObject json = new JSONObject(APPLIED_PATCHES);
            JSONArray singlePatches = json.getJSONArray("singlePatches");
            JSONArray groupPatches = json.getJSONArray("groupPatches");

            for (int i = 0; i < singlePatches.length(); i++) {
                JSONObject singlePatch = singlePatches.getJSONObject(i);
                if (singlePatch.getString("shortname").equals(patchName)) {
                    return true;
                }
            }

            for (int i = 0; i < groupPatches.length(); i++) {
                JSONArray patches = groupPatches.getJSONObject(i).getJSONArray("patches");
                for (int a  = 0; a < patches.length(); a++) {
                    JSONObject patch = patches.getJSONObject(a);
                    if (patch.getString("shortname").equals(patchName)) {
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
