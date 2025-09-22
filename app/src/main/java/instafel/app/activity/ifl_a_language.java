package instafel.app.activity;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Switch;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import instafel.app.R;
import instafel.app.ui.TileLargeSwitch;
import instafel.app.managers.PreferenceManager;
import instafel.app.utils.localization.Locales;
import instafel.app.utils.localization.LocalizationInfo;
import instafel.app.utils.localization.LocalizationUtils;
import instafel.app.utils.types.PreferenceKeys;

public class ifl_a_language extends AppCompatActivity {

    private TileLargeSwitch tileLangDevice;
    private Map<String, LocalizationInfo> localeInfos;
    private Switch tileDeviceSwitch;
    private LinearLayout languagesArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, true);
        setContentView(R.layout.ifl_at_language);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        String savedLanguageData = preferenceManager.getPreferenceString(PreferenceKeys.ifl_lang, "def");

        languagesArea = findViewById(R.id.ifl_languages_layout);
        tileLangDevice = findViewById(R.id.ifl_tile_lang_device);
        tileDeviceSwitch = tileLangDevice.getSwitchView();
        localeInfos = new HashMap<>();

        Locale localizedLang = new Locale("en");

        for (Map.Entry<String, Locales.LocaleType> entry : Locales.SUPPORTED_LOCALES.entrySet()) {
            if (entry.getKey().equals(Resources.getSystem().getConfiguration().locale.getLanguage())) {
                tileLangDevice.setSubtitleText(Resources.getSystem().getConfiguration().locale.getDisplayLanguage(localizedLang) + " (Supported)");
                break;
            } else  {
                tileLangDevice.setSubtitleText(Resources.getSystem().getConfiguration().locale.getDisplayLanguage(localizedLang) + " (Not Supported)");
            }
        }

        for (Map.Entry<String, Locales.LocaleType> entry : Locales.SUPPORTED_LOCALES.entrySet()) {
            localeInfos.put(entry.getKey(), new LocalizationInfo(ifl_a_language.this, entry.getValue()));
            languagesArea.addView(localeInfos.get(entry.getKey()).localeTile);
        }

        if (savedLanguageData.equals("def")) {
            tileDeviceSwitch.setChecked(true);
            LocalizationUtils.setVisibilityOfAllLocales(false, localeInfos);
        } else {
            tileDeviceSwitch.setChecked(false);
            LocalizationUtils.setVisibilityOfAllLocales(true, localeInfos);
            LocalizationUtils.setSubIconVisibilityOfLocale(savedLanguageData, true, localeInfos);
        }

        LocalizationUtils.setLanguageClickListeners(ifl_a_language.this, localeInfos);
        tileDeviceSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> LocalizationUtils.setStateOfDevice(ifl_a_language.this, isChecked));
        tileLangDevice.setOnClickListener(v -> LocalizationUtils.setStateOfDevice(ifl_a_language.this, !tileDeviceSwitch.isChecked()));
    }

    @Override
    public void onBackPressed() {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ui_recreate, true);
        super.onBackPressed();
    }
}