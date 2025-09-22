package instafel.app.activity.devmode.comparator;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import instafel.app.R;
import instafel.app.managers.OverridesManager;
import instafel.app.ui.TileLarge;
import instafel.app.utils.GeneralFn;

public class ifl_a_devmode_backup_comparator_menu extends AppCompatActivity {

    private JSONObject backupContent1 = null, backupContent2 = null;
    private OverridesManager overridesManager;
    private TileLarge tileBackup1, tileBackup2;
    private LinearLayout startCompare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_devmode_backup_comparator_menu);

        overridesManager = new OverridesManager(this);

        tileBackup1 = findViewById(R.id.ifl_tile_backup_1);
        tileBackup2 = findViewById(R.id.ifl_tile_backup_2);
        startCompare = findViewById(R.id.ifl_start_compare);

        tileBackup1.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            startActivityForResult(intent, 11);
        });

        tileBackup2.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            startActivityForResult(intent, 22);
        });

        startCompare.setOnClickListener(view -> {
            try {
                if (backupContent1 != null && backupContent2 != null) {
                    JSONObject activityData = new JSONObject();
                    activityData.put("backup1", backupContent1.toString());
                    activityData.put("backup2", backupContent2.toString());

                    GeneralFn.startIntentWithString(ifl_a_devmode_backup_comparator_menu.this, ifl_a_devmode_backup_comparator.class, activityData.toString());
                } else {
                    Toast.makeText(ifl_a_devmode_backup_comparator_menu.this, ifl_a_devmode_backup_comparator_menu.this.getString(R.string.ifl_a11_72), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ifl_a_devmode_backup_comparator_menu.this, ifl_a_devmode_backup_comparator_menu.this.getString(R.string.ifl_a11_70), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case 22:
                Uri backup_uri;
                if (intent != null) {
                    backup_uri = intent.getData();
                } else {
                    backup_uri = null;
                }

                if (backup_uri == null) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_38), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject readedOverrideContent = overridesManager.readBackupFile(backup_uri);
                    if (readedOverrideContent != null) {
                        backupContent1 = readedOverrideContent.getJSONObject("backup");
                        tileBackup2.setSubtitleText(this.getString(R.string.ifl_a11_71));
                    } else {
                        Toast.makeText(this, this.getString(R.string.ifl_a11_39), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_39), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                break;
            case 11:
                Uri backup_uri_2;
                if (intent != null) {
                    backup_uri_2 = intent.getData();
                } else {
                    backup_uri_2 = null;
                }

                if (backup_uri_2 == null) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_38), Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject readedOverrideContent = overridesManager.readBackupFile(backup_uri_2);
                    if (readedOverrideContent != null) {
                        backupContent1 = readedOverrideContent.getJSONObject("backup");
                        tileBackup1.setSubtitleText(this.getString(R.string.ifl_a11_71));
                    } else {
                        Toast.makeText(this, this.getString(R.string.ifl_a11_39), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_39), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                break;
        }
    }
}