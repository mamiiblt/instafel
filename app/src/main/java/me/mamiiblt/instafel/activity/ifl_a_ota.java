package me.mamiiblt.instafel.activity;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.managers.FrequencyManager;
import me.mamiiblt.instafel.managers.NotificationOtaManager;
import me.mamiiblt.instafel.ota.CheckUpdates;
import me.mamiiblt.instafel.managers.PermissionManager;
import me.mamiiblt.instafel.ota.LastCheck;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.ui.TileLargeSwitch;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.PreferenceKeys;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;

public class ifl_a_ota extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private TileLarge tileCheck, tileFreq, tileInstafelUpdater;
    private TileLargeSwitch tileSetting, tileBackgroundDownload;
    private Switch switchView, switchViewBackground;
    private NotificationOtaManager notificationOtaManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_ota);

        notificationOtaManager = new NotificationOtaManager(this);
        preferenceManager = new PreferenceManager(this);

        tileCheck = findViewById(R.id.ifl_tile_ota_check);
        tileFreq = findViewById(R.id.ifl_tile_ota_freq);
        tileSetting = findViewById(R.id.ifl_tile_ota_enable_updates);
        tileBackgroundDownload = findViewById(R.id.ifl_tile_ota_background_download);
        tileInstafelUpdater = findViewById(R.id.ifl_tile_ota_instafel_updater);
        switchViewBackground = tileBackgroundDownload.getSwitchView();
        switchView = tileSetting.getSwitchView();

        tileInstafelUpdater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFn.openInWebBrowser(ifl_a_ota.this, "https://instafel.app/about_updater");
            }
        });

        if (preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_ota_setting, false)) {
            enablePage(this);
        } else {
            disablePage(this);
            tileFreq.setVisibility(View.GONE);
            tileCheck.setVisibility(View.GONE);
            tileBackgroundDownload.setVisibility(View.GONE);
        }

        switchViewBackground.setChecked(preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_ota_background_enable, false));
        switchView.setChecked(preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_ota_setting, false));

        tileBackgroundDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBDownloadFeature(!switchViewBackground.isChecked());
            }
        });

        switchViewBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableDisableBDownloadFeature(isChecked);
            }
        });

        tileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InstafelEnv.PRODUCTION_MODE) {
                    enableDisableOtaFeature(!switchView.isChecked());
                    preferenceManager.setPreferenceLong(PreferenceKeys.ifl_ota_last_check, 0);
                    tileCheck.setSubtitleText(LastCheck.get(ifl_a_ota.this, Locale.getDefault()));
                } else {
                    Toast.makeText(ifl_a_ota.this, "This is not an production build.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (InstafelEnv.PRODUCTION_MODE) {
                    enableDisableOtaFeature(isChecked);
                    preferenceManager.setPreferenceLong(PreferenceKeys.ifl_ota_last_check, 0);
                    tileCheck.setSubtitleText(LastCheck.get(ifl_a_ota.this, Locale.getDefault()));
                } else {
                    Toast.makeText(ifl_a_ota.this, "This is not an production build.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tileFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog1 = new Dialog(ifl_a_ota.this);
                dialog1.setContentView(R.layout.ifl_dg_ota_set_freq);
                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog1.getWindow().setBackgroundDrawable(ifl_a_ota.this.getDrawable(R.drawable.ifl_dg_ota_background));
                dialog1.setCancelable(false);

                // set adapter
                String[] arraySpinner = new String[] {
                        ifl_a_ota.this.getString(R.string.ifl_a5_dia_freq_00),
                        ifl_a_ota.this.getString(R.string.ifl_a5_dia_freq_01),
                        ifl_a_ota.this.getString(R.string.ifl_a5_dia_freq_02),
                        ifl_a_ota.this.getString(R.string.ifl_a5_dia_freq_03),
                        ifl_a_ota.this.getString(R.string.ifl_a5_dia_freq_04),
                        ifl_a_ota.this.getString(R.string.ifl_a5_dia_freq_05),
                };
                Spinner s = (Spinner) dialog1.findViewById(R.id.ifl_dialog_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ifl_a_ota.this,
                        android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
                s.setSelection(FrequencyManager.getFreqId(ifl_a_ota.this));

                TextView title = dialog1.findViewById(R.id.ifl_dialog_title);
                LinearLayout button1 = dialog1.findViewById(R.id.ifl_dg_button_negative);
                LinearLayout button2 = dialog1.findViewById(R.id.ifl_dg_button_pozitive);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int idx = s.getSelectedItemPosition();
                        FrequencyManager.setFreq(ifl_a_ota.this, idx);
                        tileFreq.setSubtitleText(FrequencyManager.getFreq(ifl_a_ota.this));
                        dialog1.dismiss();
                    }
                });
                
                dialog1.show();
            }
        });

        tileCheck.setSubtitleText(LastCheck.get(this, Locale.getDefault()));
        tileFreq.setSubtitleText(FrequencyManager.getFreq(this));

        CheckUpdates.set(this, findViewById(R.id.ifl_tile_ota_check));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (PermissionManager.checkPermission(this)) {
            enablePage(this);
            preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_setting, true);
        } else {
            disablePage(this);
            preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_setting, false);
        }
    }

    public void enableDisableBDownloadFeature (boolean state) {
        if (state) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (this.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                    switchViewBackground.setChecked(false);
                } else {
                    preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_background_enable, state);
                    notificationOtaManager.createNotificationChannel();
                    switchViewBackground.setChecked(state);
                }
            } else {
                preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_background_enable, state);
                notificationOtaManager.createNotificationChannel();
                switchViewBackground.setChecked(state);
            }
        } else {
            preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_background_enable, state);
            notificationOtaManager.createNotificationChannel();
            switchViewBackground.setChecked(state);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_background_enable, true);
                switchViewBackground.setChecked(true);
                notificationOtaManager.createNotificationChannel();
            } else {
                preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_background_enable, false);
                switchViewBackground.setChecked(false);
                InstafelDialog instafelDialog = InstafelDialog.createSimpleDialog(
                        this,
                        "Permission Denied",
                        "Instafel's notification permission was denied. If this problem persists, do not forget to turn on notifications from the application settings.",
                        "Okay",
                        null,
                        null,
                        null);
                instafelDialog.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void enableDisableOtaFeature (boolean state) {
        if (state) {
            if (PermissionManager.checkPermission(ifl_a_ota.this)) {
                enablePage(ifl_a_ota.this);
                preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_setting, state);
            } else {
                disablePage(ifl_a_ota.this);
                PermissionManager.requestInstallPermission(ifl_a_ota.this);
            }
        } else {
            disablePage(ifl_a_ota.this);
            preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ota_setting, false);
        }
    }

    public void enablePage(Activity activity) {
        switchView.setChecked(true);

        tileCheck.setVisibility(View.VISIBLE);
        tileBackgroundDownload.setVisibility(View.VISIBLE);
        tileFreq.setVisibility(View.VISIBLE);
    }

    private void disablePage(Activity activity) {
        switchView.setChecked(false);

        tileCheck.setVisibility(View.GONE);
        tileBackgroundDownload.setVisibility(View.GONE);
        tileFreq.setVisibility(View.GONE);
    }
}