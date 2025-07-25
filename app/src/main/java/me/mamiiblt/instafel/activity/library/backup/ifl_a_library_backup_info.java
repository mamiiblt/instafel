package me.mamiiblt.instafel.activity.library.backup;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.api.models.AutoUpdateInfo;
import me.mamiiblt.instafel.api.models.Backup;
import me.mamiiblt.instafel.api.models.InstafelResponse;
import me.mamiiblt.instafel.api.requests.ApiCallbackInterface;
import me.mamiiblt.instafel.api.requests.ApiGetString;
import me.mamiiblt.instafel.managers.OverridesManager;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ui.LoadingBar;
import me.mamiiblt.instafel.ui.PageContentArea;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;

public class ifl_a_library_backup_info extends AppCompatActivity implements ApiCallbackInterface{

    private TileLarge tileName, tileDescription, tileLastUpdated, tileAboutAuthor, tileApplyBackup, tileChangelog, tileVersion;
    private PageContentArea areaContent, areaLoading;
    private InstafelDialog loadingDialog;
    private Backup backup;
    private OverridesManager overridesManager;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_library_backup_info);

        overridesManager = new OverridesManager(this);
        preferenceManager = new PreferenceManager(this);
        tileName = findViewById(R.id.ifl_tile_backup_library_name);
        tileDescription = findViewById(R.id.ifl_tile_backup_library_desc);
        tileLastUpdated = findViewById(R.id.ifl_tile_backup_library_lastUpdated);
        tileAboutAuthor = findViewById(R.id.ifl_tile_backup_library_aboutAuthor);
        tileApplyBackup = findViewById(R.id.ifl_tile_backup_library_applyBackup);
        tileChangelog = findViewById(R.id.ifl_tile_backup_library_changelog);
        tileVersion = findViewById(R.id.ifl_tile_backup_library_version);
        areaContent = findViewById(R.id.ifl_content_area);
        areaLoading = findViewById(R.id.ifl_loading_page);

        InstafelDialog instafelDialog = new InstafelDialog(this);
        this.loadingDialog = instafelDialog;
        instafelDialog.addSpace("top_space", 25);
        LoadingBar loadingBar = new LoadingBar(this);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
        int i = (int) ((25 * getResources().getDisplayMetrics().density) + 0.5f);
        marginLayoutParams.setMargins(i, 0, i, 0);
        loadingBar.setLayoutParams(marginLayoutParams);
        this.loadingDialog.addCustomView("loading_bar", loadingBar);
        this.loadingDialog.addSpace("button_top_space", 25);

        try {
            Intent intent = getIntent();
            String backupListItemUnparsed = intent.getStringExtra("data");

            if (!backupListItemUnparsed.equals("CANNOT_BE_CONVERTED")) {
                JSONObject backupItem = new JSONObject(backupListItemUnparsed);
                backup = new Backup(backupItem.getString("id"), backupItem.getString("name"), backupItem.getString("author"));

                ApiGetString apiGetString = new ApiGetString(this, 11);
                apiGetString.execute("https://raw.githubusercontent.com/instafel/backups/main/" + backup.getId() + "/manifest.json");

            } else {
                Toast.makeText(this, this.getString(R.string.ifl_a11_17), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, this.getString(R.string.ifl_a11_17), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void getResponse(String rawResponse, int taskId) {
        if (taskId == 11) {
            if (rawResponse != null) {
                try {
                    JSONObject parsedJson = new JSONObject(rawResponse);
                    JSONObject parsedManifest = parsedJson.getJSONObject("manifest");
                    JSONObject parsedManifestOptional = parsedManifest.getJSONObject("optional");
                    backup.setManifestVersion(parsedJson.getInt("manifest_version"));
                    backup.setBackupVersion(parsedManifest.getInt("backup_version"));
                    backup.setChangelog(parsedManifest.getString("changelog"));
                    backup.setLastEdit(parsedManifest.getString("last_updated"));
                    backup.setDescription(parsedManifest.getString("description"));
                    backup.setOptionalShowAuthorSocials(parsedManifestOptional.getBoolean("show_author_socials"));
                    backup.setVersionName(parsedManifest.getString("version_name"));

                    if (backup.isOptionalShowAuthorSocials()) {
                        tileAboutAuthor.setSpaceBottom("gone");
                        JSONObject parsedManifestOptionalSocial = parsedManifest.getJSONObject("optional_values").getJSONObject("author_socials");
                        JSONObject authorObject = new JSONObject();
                        authorObject.put("name", backup.getAuthor());
                        if (parsedManifestOptionalSocial.has("github")) {
                            authorObject.put("github", parsedManifestOptionalSocial.get("github"));
                        }
                        if (parsedManifestOptionalSocial.has("instagram")) {
                            authorObject.put("instagram", parsedManifestOptionalSocial.get("instagram"));
                        }
                        if (parsedManifestOptionalSocial.has("medium")) {
                            authorObject.put("medium", parsedManifestOptionalSocial.get("medium"));
                        }
                        if (parsedManifestOptionalSocial.has("x")) {
                            authorObject.put("x", parsedManifestOptionalSocial.get("x"));
                        }

                        backup.setAuthorSocials(authorObject);
                    } else {
                        tileLastUpdated.setSpaceBottom("gone");
                        tileAboutAuthor.setVisibility(View.GONE);
                    }

                    if (backup.getManifestVersion() <= 3) {
                        tileName.setSubtitleText(backup.getName());
                        tileDescription.setSubtitleText(backup.getDescription());
                        tileLastUpdated.setSubtitleText(backup.getLastEdit());
                        tileVersion.setSubtitleText(backup.getVersionName());
                        tileChangelog.setSubtitleText(backup.getChangelog());
                        tileAboutAuthor.setSubtitleText(backup.getAuthor());
                        tileAboutAuthor.setOnClickListener(view -> GeneralFn.startIntentWithString(ifl_a_library_backup_info.this, ifl_a_library_backup_info_author.class, backup.getAuthorSocials().toString()));
                        tileApplyBackup.setOnClickListener(view -> {
                            ApiGetString apiGetString = new ApiGetString(ifl_a_library_backup_info.this, 14);
                            apiGetString.execute("https://raw.githubusercontent.com/instafel/backups/main/" + backup.getId() + "/backup.ibackup");
                        });
                        areaContent.setVisibility(View.VISIBLE);
                        areaLoading.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "This backup requires newer version of Instafel (manifest: " + backup.getManifestVersion() + ")", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_17), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, this.getString(R.string.ifl_a11_17), Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == 14) {
            if (rawResponse != null) {
                applyBackup(rawResponse);
            } else {
                Toast.makeText(this, this.getString(R.string.ifl_a11_22), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void applyBackup(String rawResponse) {
        try {
            JSONObject contentBackup = new JSONObject(rawResponse);
            String writeState = overridesManager.writeContentIntoOverridesFile(contentBackup);
            if (writeState.equals("SUCCESS")) {
                Toast.makeText(this, this.getString(R.string.ifl_a11_44, backup.getName()), Toast.LENGTH_SHORT).show();
                preferenceManager.setPreferenceString(
                        PreferenceKeys.ifl_backup_update_value,
                        new AutoUpdateInfo(backup.getId(), backup.getName(), backup.getBackupVersion()).exportAsJsonString());
            } else {
                Toast.makeText(this, this.getString(R.string.ifl_a11_22) + "\n\n" + writeState, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, this.getString(R.string.ifl_a11_22), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void getResponse(InstafelResponse instafelResponse, int taskId) {

    }

}