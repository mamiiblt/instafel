package instafel.app.activity;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.getActivityLocale;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.os.LocaleList;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Switch;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import instafel.app.InstafelEnv;
import instafel.app.R;
import instafel.app.ui.TileLargeSwitch;
import instafel.app.managers.PreferenceManager;
import instafel.app.utils.localization.LocaleInfoTile;
import instafel.app.utils.localization.LocalizationUtils;
import instafel.app.utils.types.PreferenceKeys;

public class ifl_a_language extends AppCompatActivity {

    private TileLargeSwitch tileLangDevice;
    private Map<Locale, LocaleInfoTile> localeTiles;
    private Switch tileDeviceSwitch;
    private LinearLayout languagesArea;
    private LocaleList supportedLocaleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, true);
        setContentView(R.layout.ifl_at_language);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        String savedLanguageData = preferenceManager.getPreferenceString(PreferenceKeys.ifl_lang_rw, "def");

        languagesArea = findViewById(R.id.ifl_languages_layout);
        tileLangDevice = findViewById(R.id.ifl_tile_lang_device);
        tileDeviceSwitch = tileLangDevice.getSwitchView();
        localeTiles = new HashMap<>();
        supportedLocaleList = InstafelEnv.getSupportedLocaleList();

        Locale currentLocale = getActivityLocale(this);

        for (int i = 0; i < supportedLocaleList.size(); i++) {
            Locale locale = supportedLocaleList.get(i);
            if (currentLocale.equals(locale)) {
                tileLangDevice.setSubtitleText(
                        currentLocale.getDisplayLanguage(currentLocale) + ", " + currentLocale.getDisplayCountry(currentLocale) + " (" + this.getString(R.string.ifl_a6_02) + ") "
                );
                break;
            } else {
                tileLangDevice.setSubtitleText(
                        currentLocale.getDisplayLanguage(currentLocale) + " (" + this.getString(R.string.ifl_a6_03) + ") "
                );
            }
        }

        for (int i = 0; i < supportedLocaleList.size(); i++) {
            Locale locale = supportedLocaleList.get(i);
            localeTiles.put(locale, new LocaleInfoTile(ifl_a_language.this, locale));
            languagesArea.addView(localeTiles.get(locale).localeTile);
        }

        if (savedLanguageData.equals("def")) {
            tileDeviceSwitch.setChecked(true);
            LocalizationUtils.setVisibilityOfAllLocales(false, localeTiles);
        } else {
            tileDeviceSwitch.setChecked(false);
            LocalizationUtils.setVisibilityOfAllLocales(true, localeTiles);
            LocalizationUtils.setSubIconVisibilityOfLocale(savedLanguageData, true, localeTiles);
        }

        LocalizationUtils.setLanguageClickListeners(ifl_a_language.this, localeTiles);
        tileDeviceSwitch.setOnCheckedChangeListener((compoundButton, isChecked)
                -> LocalizationUtils.setStateOfDevice(ifl_a_language.this, isChecked));
    }

    @Override
    public void onBackPressed() {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ui_recreate, true);
        super.onBackPressed();
    }
}