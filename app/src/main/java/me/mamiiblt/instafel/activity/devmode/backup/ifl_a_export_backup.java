package me.mamiiblt.instafel.activity.devmode.backup;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.localization.LocalizationUtils.updateIflLocale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.Normalizer;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.OverridesManager;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ui.TileLargeEditText;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;

public class ifl_a_export_backup extends AppCompatActivity {

    OverridesManager overridesManager;
    PreferenceManager preferenceManager;
    LinearLayout exportButton;
    TileLargeEditText tileBackupName, tileAuthorName, tileVersion, tileChangelog;
    EditText tileVersionEditText, tileAuthorNameEditText, tileBackupNameEditText, tileChangelogEditText;
    JSONObject backupFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_export_backup);

        this.overridesManager = new OverridesManager(this);
        this.preferenceManager = new PreferenceManager(this);

        this.tileBackupName = findViewById(R.id.ifl_tile_export_backup_name);
        this.tileAuthorName = findViewById(R.id.ifl_tile_export_author_name);
        this.tileVersion = findViewById(R.id.ifl_tile_export_backup_version);
        this.tileChangelog = findViewById(R.id.ifl_tile_export_backup_changelog);
        this.exportButton = findViewById(R.id.ifl_button_exportbackup);

        tileVersionEditText = tileVersion.getEditTextView();
        tileAuthorNameEditText = tileAuthorName.getEditTextView();
        tileBackupNameEditText = tileBackupName.getEditTextView();
        tileChangelogEditText = tileChangelog.getEditTextView();

        tileVersionEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        tileVersionEditText.setInputType(131073);
        tileVersionEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        tileVersionEditText.setMaxLines(1);
        tileVersionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tileVersionEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                fixVersionNameString(tileVersionEditText);
            }
            return false;
        });

        tileVersionEditText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                fixVersionNameString(tileVersionEditText);
            }
        });

        tileAuthorNameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        tileAuthorNameEditText.setInputType(131073);
        tileAuthorNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        tileAuthorNameEditText.setMaxLines(1);
        tileAuthorNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        tileBackupNameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        tileBackupNameEditText.setInputType(131073);
        tileBackupNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        tileBackupNameEditText.setMaxLines(1);
        tileBackupNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        tileChangelogEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2500)});
        tileChangelogEditText.setInputType(131073);
        tileChangelogEditText.setMaxLines(10);
        tileChangelogEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i != 6 && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                return false;
            }
            tileAuthorNameEditText.append("\n");
            return true;
        });

        exportButton.setOnClickListener(view -> {
            if (!tileAuthorNameEditText.getText().toString().isEmpty() && !tileBackupNameEditText.getText().toString().isEmpty() && !tileVersionEditText.getText().toString().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_TITLE, createFileName(tileBackupNameEditText.getText().toString(), tileVersionEditText.getText().toString()) +  ".ibackup");
                intent.setType("application/x-ibackup");
                startActivityForResult(intent, 18);
            } else {
                InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_export_backup.this, "Alert", ifl_a_export_backup.this.getString(R.string.ifl_a11_49));
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 18:
                Uri backup_uri;
                if (intent != null) {
                    backup_uri = intent.getData();
                } else {
                    backup_uri = null;
                }

                if (backup_uri == null) {
                    InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_export_backup.this, "Alert", this.getString(R.string.ifl_a4_04));
                    return;
                }

                if (resultCode == -1) {

                    try {
                        JSONObject contentOverride = overridesManager.readOverrideFile();
                        if (contentOverride == null) {
                            InstafelDialog.createSimpleAlertDialogNoFinish(this, "Alert", this.getString(R.string.ifl_a4_39));
                        } else {
                            if (!tileAuthorNameEditText.getText().toString().isEmpty() && !tileBackupNameEditText.getText().toString().isEmpty() && !tileVersionEditText.getText().toString().isEmpty()) {
                                backupFile = new JSONObject();
                                backupFile.put("manifest_version", 1);
                                JSONObject backupInfo = new JSONObject();
                                backupInfo.put("id", JSONObject.NULL);
                                backupInfo.put("name", tileBackupNameEditText.getText().toString().trim());
                                backupInfo.put("author", tileAuthorNameEditText.getText().toString().trim());
                                backupInfo.put("version", tileVersionEditText.getText().toString().trim());

                                if (tileChangelogEditText.getText().toString().isEmpty()) {
                                    backupInfo.put("changelog", JSONObject.NULL);
                                } else {
                                    backupInfo.put("changelog", tileChangelogEditText.getText().toString());
                                }

                                backupFile.put("info", backupInfo);
                                backupFile.put("backup", contentOverride);
                                boolean status = overridesManager.writeContentIntoBackupFile(backup_uri, backupFile);
                                if (status) {
                                    InstafelDialog.createSimpleAlertDialog(ifl_a_export_backup.this, "Success", this.getString(R.string.ifl_a4_40, String.valueOf(contentOverride.length())));
                                } else {
                                    InstafelDialog.createSimpleAlertDialogNoFinish(this, "Alert", this.getString(R.string.ifl_a4_41));
                                }
                            } else {
                                InstafelDialog.createSimpleAlertDialogNoFinish(ifl_a_export_backup.this, "Alert", this.getString(R.string.ifl_a11_49));
                            }
                        }
                    } catch (Exception e) {
                        InstafelDialog.createSimpleAlertDialogNoFinish(this, "Alert", this.getString(R.string.ifl_a4_42));
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void fixVersionNameString(EditText editText) {
        String tempVal = editText.getText().toString().trim();
        if (!tempVal.isEmpty()) {
            char fchar = tempVal.charAt(0);
            if (fchar == 'v') {
                editText.setText(fixVersionNameCharacters(tempVal));
            } else {
                editText.setText(fixVersionNameCharacters("v" + tempVal));
            }
        } else {
            Toast.makeText(ifl_a_export_backup.this, this.getString(R.string.ifl_a11_50), Toast.LENGTH_SHORT).show();
        }
    }

    private String fixVersionNameCharacters(String val) {
        String normalized = Normalizer.normalize(val, Normalizer.Form.NFKD);
        normalized = normalized.toLowerCase();
        normalized = normalized.replaceAll("[^\\p{L}\\p{N}\\s]", "");
        normalized = normalized.replaceAll("\\s+", "_");
        normalized = normalized.replaceAll("[\\\\/:*?\"<>|]", "_");
        normalized = normalized.replaceAll("^_+|_+$", "");

        if (normalized.isEmpty()) {
            normalized = "null";
        }

        return normalized;
    }

    private String createFileName(String backupName, String version) {
        String normalized_backupName = Normalizer.normalize(backupName, Normalizer.Form.NFKD);
        normalized_backupName = normalized_backupName.toLowerCase();
        normalized_backupName = normalized_backupName.replaceAll("[^\\p{L}\\p{N}\\s]", "");
        normalized_backupName = normalized_backupName.replaceAll("\\s+", "_");
        normalized_backupName = normalized_backupName.replaceAll("[\\\\/:*?\"<>|]", "_");
        normalized_backupName = normalized_backupName.replaceAll("^_+|_+$", "");

        if (normalized_backupName.isEmpty()) {
            normalized_backupName = "ifl_backup";
        }

        String normalized_versionName = Normalizer.normalize(version, Normalizer.Form.NFKD);
        normalized_versionName = normalized_versionName.toLowerCase();
        normalized_versionName = normalized_versionName.replaceAll("[^\\p{L}\\p{N}\\s]", ""); // Sadece harf, sayı ve boşluk bırak
        normalized_versionName = normalized_versionName.replaceAll("\\s+", "_");
        normalized_versionName = normalized_versionName.replaceAll("[\\\\/:*?\"<>|]", "_");
        normalized_versionName = normalized_versionName.replaceAll("^_+|_+$", "");

        if (normalized_versionName.isEmpty()) {
            normalized_versionName = "v1";
        }

        return normalized_backupName + "_" + normalized_versionName;
    }
}
