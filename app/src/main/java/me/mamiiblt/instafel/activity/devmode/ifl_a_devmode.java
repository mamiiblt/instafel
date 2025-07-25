package me.mamiiblt.instafel.activity.devmode;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.activity.devmode.analyzer.ifl_a_devmode_backup_analyzer_menu;
import me.mamiiblt.instafel.activity.devmode.backup.ifl_a_export_backup;
import me.mamiiblt.instafel.activity.devmode.comparator.ifl_a_devmode_backup_comparator_menu;
import me.mamiiblt.instafel.managers.OverridesManager;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;
import me.mamiiblt.instafel.utils.dialog.InstafelDialogMargins;
import me.mamiiblt.instafel.utils.dialog.InstafelDialogTextType;

public class ifl_a_devmode extends AppCompatActivity {

    PreferenceManager preferenceManager;
    OverridesManager overridesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridesManager = new OverridesManager(this);
        this.preferenceManager = new PreferenceManager(this);

        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_devmode);

        findViewById(R.id.ifl_tile_exp_import).setOnClickListener(v -> GeneralFn.startIntent(ifl_a_devmode.this, ifl_a_devmode_import.class));
        findViewById(R.id.ifl_tile_exp_export).setOnClickListener(v -> GeneralFn.startIntent(ifl_a_devmode.this, ifl_a_export_backup.class));
        findViewById(R.id.ifl_tile_exp_comperator).setOnClickListener(view -> GeneralFn.startIntent(ifl_a_devmode.this, ifl_a_devmode_backup_comparator_menu.class));

        findViewById(R.id.ifl_tile_exp_analyzer).setOnClickListener(v -> {
            JSONObject overrideContent = overridesManager.readOverrideFile();
            if (overrideContent != null) {
                GeneralFn.startIntent(ifl_a_devmode.this, ifl_a_devmode_backup_analyzer_menu.class);
            } else {
                InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode.this, "Alert", ifl_a_devmode.this.getString(R.string.ifl_a4_34));
            }
        });

        findViewById(R.id.ifl_tile_exp_develeov).setOnClickListener(v -> {
            if (overridesManager.existsOverrideFile()) {
                InstafelDialog instafelDialog = new InstafelDialog(ifl_a_devmode.this);
                instafelDialog.addSpace("top_space", 25);
                instafelDialog.addTextView(
                        "dialog_title",
                        ifl_a_devmode.this.getString(R.string.ifl_a4_35),
                        30,
                        0,
                        InstafelDialogTextType.TITLE,
                        new InstafelDialogMargins(ifl_a_devmode.this, 0, 0));
                instafelDialog.addSpace("mid_space", 20);
                instafelDialog.addTextView(
                        "dialog_desc",
                        ifl_a_devmode.this.getString(R.string.ifl_a4_36),
                        16,
                        310,
                        InstafelDialogTextType.DESCRIPTION,
                        new InstafelDialogMargins(ifl_a_devmode.this, 24, 24));
                instafelDialog.addSpace("button_top_space", 20);
                instafelDialog.addPozitiveAndNegativeButton(
                        "buttons",
                        ifl_a_devmode.this.getString(R.string.ifl_a4_37),
                        ifl_a_devmode.this.getString(R.string.ifl_a4_38),
                        v2 -> {
                            instafelDialog.dismiss();
                            overridesManager.deleteOverrideFile();
                            Toast.makeText(ifl_a_devmode.this, R.string.ifl_a4_13, Toast.LENGTH_SHORT).show();
                        },
                        v1 -> instafelDialog.dismiss());
                instafelDialog.addSpace("bottom_space", 27);
                instafelDialog.show();
            } else {
                InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode.this, "Alert", ifl_a_devmode.this.getString(R.string.ifl_a4_14));
            }
        });
    }
}