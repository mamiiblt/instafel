package instafel.app.utils.localization;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.Toast;

import instafel.app.utils.GeneralFn;

import java.util.Locale;

public class LocalizedStringGetter {

    public static String getLocalizedString(Activity _activity, Locale locale, String resLabel, Object... params) {
        try {
            Resources appResources = GeneralFn.getAppResourcesWithConf(_activity, locale);
            if (params.length != 0) {
                assert appResources != null;
                return appResources.getString(
                        GeneralFn.getStringResId(_activity, appResources, resLabel),
                        params
                );
            } else {
                assert appResources != null;
                return appResources.getString(
                        GeneralFn.getStringResId(_activity, appResources, resLabel)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_activity, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static String getDialogLocalizedString(Activity _activity, Locale locale, String resourceName) {
        try {
            Resources appResources = GeneralFn.getAppResourcesWithConf(_activity, locale);
            return appResources.getString(
                    GeneralFn.getStringResId(_activity, appResources, resourceName)
            ).replace("\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(Activity activity, String resLabel) {
        Resources appResources = GeneralFn.getAppResources(activity);
        return appResources.getString(
                GeneralFn.getStringResId(activity, appResources, resLabel)
        );
    }
}
