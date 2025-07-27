package me.mamiiblt.instafel.activity.devmode.analyzer;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.localization.LocalizationUtils.updateIflLocale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.OverridesManager;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.GeneralFn;

public class ifl_a_devmode_backup_analyzer_menu extends AppCompatActivity {

    private JSONObject overrideContent = null;
    private Uri mappingContentUri = null;
    private TextView buttonText;
    private OverridesManager overridesManager;
    private TileLarge tileCustomOverride, tileCustomMapping;
    private LinearLayout startAnalyze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_devmode_backup_analyzer_menu);

        overridesManager = new OverridesManager(this);
        buttonText = findViewById(R.id.ifl_button_text);

        tileCustomOverride = findViewById(R.id.ifl_tile_use_custom_override);
        tileCustomMapping = findViewById(R.id.ifl_tile_use_custom_mapping);
        startAnalyze = findViewById(R.id.ifl_start_analyze);
        updateButtonText();

        tileCustomOverride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/octet-stream");
                startActivityForResult(intent, 11);
            }
        });

        tileCustomMapping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                startActivityForResult(intent, 22);
            }
        });

        startAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject activityData = new JSONObject();

                    if (overrideContent != null) {
                        activityData.put("override", overrideContent.toString());
                    } else {
                        activityData.put("override", new JSONObject().toString());
                    }

                    if (mappingContentUri != null) {
                        activityData.put("mapping", mappingContentUri.toString());
                    } else {
                        activityData.put("mapping", "NOT_ANY_MAPPING_SELECTED");
                    }

                    GeneralFn.startIntentWithString(ifl_a_devmode_backup_analyzer_menu.this, ifl_a_devmode_backup_analyzer.class, activityData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ifl_a_devmode_backup_analyzer_menu.this, ifl_a_devmode_backup_analyzer_menu.this.getString(R.string.ifl_a11_37), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
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
                        overrideContent = readedOverrideContent.getJSONObject("backup");
                        updateButtonText();
                    } else {
                        Toast.makeText(this, this.getString(R.string.ifl_a11_39), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_39), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                break;
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
                    JSONArray readedMappingContent = overridesManager.readMappingFileFromUri(backup_uri);
                    if (readedMappingContent != null) {
                        mappingContentUri = backup_uri;
                        updateButtonText();
                    } else {
                        Toast.makeText(this, this.getString(R.string.ifl_a11_40), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_40), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                break;
        }
    }

    private void updateButtonText() {
        if (overrideContent != null && mappingContentUri != null) {
            buttonText.setText(this.getString(R.string.ifl_a4_31));
        } else if (overrideContent != null) {
            buttonText.setText(this.getString(R.string.ifl_a4_29));
        } else if (mappingContentUri != null) {
            buttonText.setText(this.getString(R.string.ifl_a4_30));
        } else {
            buttonText.setText(this.getString(R.string.ifl_a4_28));
        }
    }
}