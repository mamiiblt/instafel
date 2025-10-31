package me.mamiiblt.instafel.updater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

public class SetupActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivityIfAvailable(this);
        } else {
            setTheme(R.style.Base_Theme_InstafelUpdater);
        }

        setContentView(R.layout.activity_setup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RadioGroup radioGroupInstallType = findViewById(R.id.install_type_radio_group);
        RadioGroup radioGroupIMethod = findViewById(R.id.method_radio_group);

        radioGroupInstallType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_unclone) {
                editor.putInt("install_type_i", 11);
            } else if (checkedId == R.id.radio_clone) {
                editor.putInt("install_type_i", 22);
            }
            editor.apply();
        });

        radioGroupIMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_root) {
                editor.putString("checker_method", "root");
            } else if (checkedId == R.id.radio_shizuku) {
                editor.putString("checker_method", "shi");
            }
            editor.apply();
        });
    }

    public void next(View view) {

        if (!preferences.getString("checker_method", "NULL").equals("NULL") && preferences.getInt("install_type_i", 0) != 0) {
            editor.putInt("checker_interval_i", 4);
            editor.apply();
            Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(this.getString(R.string.warning))
                    .setMessage(this.getString(R.string.warning_desc))
                    .setNegativeButton(android.R.string.yes, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}