package me.mamiiblt.instafel.activity.devmode;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.localization.LocalizationUtils.updateIflLocale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.OverridesManager;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;

public class ifl_a_devmode_import extends AppCompatActivity {

    OverridesManager overridesManager;
    TileLarge tileFull, tileMerge, tileOldBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_devmode_import);

        this.overridesManager = new OverridesManager(this);
        tileFull = findViewById(R.id.ifl_tile_import_full);
        tileMerge = findViewById(R.id.ifl_tile_import_merge);
        tileOldBackup = findViewById(R.id.ifl_tile_import_old_backups);

        tileFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/octet-stream");
                startActivityForResult(intent, 15);
            }
        });

        tileFull.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                boolean status = overridesManager.createMappingFileDebug();
                if (status) {
                    Toast.makeText(ifl_a_devmode_import.this, "Created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ifl_a_devmode_import.this, "Couldn't created", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        tileOldBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                startActivityForResult(intent, 29);
            }
        });

        tileMerge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/octet-stream");
                startActivityForResult(intent, 22);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 29:
                Uri backup_uri_3;
                if (intent != null) {
                    backup_uri_3 = intent.getData();
                } else {
                    backup_uri_3 = null;
                }

                if (backup_uri_3 == null) {
                    Toast.makeText(this, R.string.ifl_a4_09, Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject contentBackup = overridesManager.readBackupFile(backup_uri_3);
                    if (contentBackup != null) {
                        String state = overridesManager.writeContentIntoOverridesFile(contentBackup);
                        if (state.equals("SUCCESS")) {
                            InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Success", this.getString(R.string.ifl_a11_43,Integer.toString(contentBackup.length())));
                        } else {
                            InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Alert", this.getString(R.string.ifl_a11_42) + "\n\n" + state);
                        }
                    } else {
                        InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Alert", this.getString(R.string.ifl_a11_42));
                    }
                } catch (Exception e) {
                    InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Error", this.getString(R.string.ifl_a11_42) + "\n" + e.getMessage());
                    e.printStackTrace();
                }
                break;
            case 22:
                Uri backup_uri_2;
                if (intent != null) {
                    backup_uri_2 = intent.getData();
                } else {
                    backup_uri_2 = null;
                }

                if (backup_uri_2 == null) {
                    Toast.makeText(this, R.string.ifl_a4_09, Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject contentBackup = overridesManager.readBackupFile(backup_uri_2);
                    if (contentBackup != null) {
                        JSONObject contentOverrides = overridesManager.readOverrideFile();
                        if (contentOverrides != null) {
                            Iterator<String> keysBackup = contentBackup.getJSONObject("backup").keys();
                            Iterator<String> keysOverrides = contentOverrides.keys();

                            Set<String> setBackup = new HashSet<>();
                            Set<String> setOverrides = new HashSet<>();

                            while (keysBackup.hasNext()) {
                                setBackup.add(keysBackup.next());
                            }

                            while (keysOverrides.hasNext()) {
                                setOverrides.add(keysOverrides.next());
                            }

                            Set<String> setDifferent = new HashSet<>(setBackup);
                            setDifferent.removeAll(setOverrides);

                            contentOverrides.remove("_qe_overrides_");
                            for (String key : setDifferent) {
                                Log.v("Instafel", key + " : " + contentBackup.getJSONObject("backup").get(key));
                                contentOverrides.put(key, contentBackup.getJSONObject("backup").get(key));
                            }
                            contentOverrides.put("_qe_overrides_", new JSONArray().toString());
                            String state = overridesManager.writeContentIntoOverridesFile(contentOverrides);
                            if (state.equals("SUCCESS")) {
                                JSONObject infoObject = contentBackup.getJSONObject("info");
                                InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Success", this.getString(R.string.ifl_a11_46, infoObject.getString("name"), Integer.toString(contentOverrides.length())));
                            } else {
                                InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Error", this.getString(R.string.ifl_a11_42) + " 4\n\n" + state);
                            }
                        } else {
                            InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Error", this.getString(R.string.ifl_a11_42) + " 3");
                        }
                    } else {
                        InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Error", this.getString(R.string.ifl_a11_42) + " 2");
                    }
                } catch (Exception e) {
                    InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Error", this.getString(R.string.ifl_a11_42) + "\n" + e.getMessage());
                    e.printStackTrace();
                }
                break;
            case 15:
                Uri backup_uri;
                if (intent != null) {
                    backup_uri = intent.getData();
                } else {
                    backup_uri = null;
                }

                if (backup_uri == null) {
                    Toast.makeText(this, R.string.ifl_a4_09, Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject contentBackup = overridesManager.readBackupFile(backup_uri);
                    if (contentBackup != null) {
                        String state = overridesManager.writeContentIntoOverridesFile(contentBackup);
                        if (state.equals("SUCCESS")) {
                            JSONObject infoObject = contentBackup.getJSONObject("info");
                            InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Success", this.getString(R.string.ifl_a11_46, infoObject.getString("name"), Integer.toString(contentBackup.getJSONObject("backup").length())));
                        } else {
                            InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Alert", this.getString(R.string.ifl_a11_42) + " (ERR_CODE 3)\n\n" + state);
                        }
                    } else {
                        InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Alert", this.getString(R.string.ifl_a11_42) + " (ERR_CODE 2)");
                    }
                } catch (Exception e) {
                    InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_devmode_import.this, "Alert", this.getString(R.string.ifl_a11_42) + " (ERR_CODE X)\n\n" + e.getMessage());
                    e.printStackTrace();
                }

                break;
        }
    }
}