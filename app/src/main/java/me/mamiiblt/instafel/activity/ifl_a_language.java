package me.mamiiblt.instafel.activity;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Locale;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.ui.TileLargeSwitch;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.Localizator;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.utils.PreferenceKeys;

public class ifl_a_language extends AppCompatActivity {

    private TileLarge tileEnglish, tileTurkish, tileGreece, tileDeutch, tileFrench, tileHungary, tileHindi, tileSpanish, tilePortugal, tileAzerbaijan, tilePolish;
    private TileLargeSwitch tileDeviceNew;
    private Switch tileDeviceSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, true);
        setContentView(R.layout.ifl_at_language);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        String prefData = preferenceManager.getPreferenceString(PreferenceKeys.ifl_lang, "def");

        tileDeviceNew = findViewById(R.id.ifl_tile_lang_device);
        tileDeviceSwitch = tileDeviceNew.getSwitchView();
        tileEnglish = findViewById(R.id.ifl_tile_lang_english);
        tileTurkish = findViewById(R.id.ifl_tile_lang_turkish);
        tileGreece = findViewById(R.id.ifl_tile_lang_greece);
        tileDeutch = findViewById(R.id.ifl_tile_lang_deutch);
        tileFrench = findViewById(R.id.ifl_tile_lang_france);
        tileHungary = findViewById(R.id.ifl_tile_lang_hungary);
        tileHindi = findViewById(R.id.ifl_tile_lang_hindi);
        tileSpanish = findViewById(R.id.ifl_tile_lang_spanish);
        tilePortugal = findViewById(R.id.ifl_tile_lang_portugal);
        tileAzerbaijan = findViewById(R.id.ifl_tile_lang_azerbaijan);
        tilePolish = findViewById(R.id.ifl_tile_lang_polish);

        Locale langLocale = new Locale("en");
        String notSupportedText = Resources.getSystem().getConfiguration().locale.getDisplayLanguage(langLocale) + " (Not Supported)";

        for (int i = 0; i < Localizator.supportedLangs.length; i++) {
            if (Localizator.supportedLangs[i].equals(Resources.getSystem().getConfiguration().locale.getLanguage())) {
                tileDeviceNew.setSubtitleText(Resources.getSystem().getConfiguration().locale.getDisplayName(langLocale));
                break;
            } else {
                tileDeviceNew.setSubtitleText(notSupportedText);
            }
        }

        if (prefData.equals("def")) {
            tileDeviceSwitch.setChecked(true);
            setVisibilities(View.GONE, tileEnglish, tileTurkish, tileGreece, tileDeutch, tileFrench, tileHungary, tileHindi, tileSpanish, tilePortugal, tileAzerbaijan, tilePolish);
        } else {
            tileDeviceSwitch.setChecked(false);
            setVisibilities(View.VISIBLE, tileEnglish, tileTurkish, tileGreece, tileDeutch, tileFrench, tileHungary, tileHindi, tileSpanish, tilePortugal, tileAzerbaijan, tilePolish);
        }

        switch (prefData) {
            case "def":
                tileDeviceSwitch.setChecked(true);
                break;
            case "en":
                tileEnglish.setVisiblitySubIcon("visible");
                break;
            case "tr":
                tileTurkish.setVisiblitySubIcon("visible");
                break;
            case "el":
                tileGreece.setVisiblitySubIcon("visible");
                break;
            case "de":
                tileDeutch.setVisiblitySubIcon("visible");
                break;
            case "fr":
                tileFrench.setVisiblitySubIcon("visible");
                break;
            case "hu":
                tileHungary.setVisiblitySubIcon("visible");
                break;
            case "hi":
                tileHindi.setVisiblitySubIcon("visible");
                break;
            case "es":
                tileSpanish.setVisiblitySubIcon("visible");
                break;
            case "pt":
                tilePortugal.setVisiblitySubIcon("visible");
                break;
            case "az":
                tileAzerbaijan.setVisiblitySubIcon("visible");
                break;
            case "pl":
                tilePolish.setVisiblitySubIcon("visible");
                break;
        }

        tileDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setState(isChecked);
            }
        });

        tileDeviceNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(!tileDeviceSwitch.isChecked());
            }
        });

        setLanguageClickListener(tileEnglish, "en",0);
        setLanguageClickListener(tileTurkish, "tr", 1);
        setLanguageClickListener(tileDeutch, "de", 2);
        setLanguageClickListener(tileGreece,"el", 3);
        setLanguageClickListener(tileFrench, "fr", 4);
        setLanguageClickListener(tileHungary, "hu", 5);
        setLanguageClickListener(tileHindi, "hi", 6);
        setLanguageClickListener(tileSpanish, "es", 7);
        setLanguageClickListener(tilePortugal, "pt", 8);
        setLanguageClickListener(tileAzerbaijan, "az", 9);
        setLanguageClickListener(tilePolish, "pl", 10);

    }

    public void setVisibilities(int visibility, TileLarge... tiles) {
        for (TileLarge tile : tiles) {
            tile.setVisibility(visibility);
        }
    }

    private void setLanguageClickListener(TileLarge tileLanguage, String langCode, int id) {
        tileLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Localizator.writeLangSh(ifl_a_language.this, langCode);
                Localizator.enableItem(ifl_a_language.this, id);
                recreate();
            }
        });
    }

    public void setState(boolean state) {
        tileDeviceSwitch.setChecked(state);
        if (state) {
            Localizator.writeLangSh(this, "def");
            updateIflLocale(this, true);
            recreate();
        } else {
            Localizator.writeLangSh(this, "en");
            tileEnglish.setVisiblitySubIcon("visible");
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ui_recreate, true);
        super.onBackPressed();
    }
}