package me.mamiiblt.instafel.activity.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.api.models.BackupListItem;
import me.mamiiblt.instafel.api.models.InstafelResponse;
import me.mamiiblt.instafel.api.requests.ApiCallbackInterface;
import me.mamiiblt.instafel.api.requests.ApiGetAdmin;
import me.mamiiblt.instafel.api.requests.ApiPostAdmin;
import me.mamiiblt.instafel.managers.OverridesManager;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ui.LoadingBar;
import me.mamiiblt.instafel.ui.PageContentArea;
import me.mamiiblt.instafel.ui.PageTitle;
import me.mamiiblt.instafel.ui.TileCompact;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.Localizator;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;

public class ifl_a_admin_action_updatebackup extends AppCompatActivity implements ApiCallbackInterface {

    PageContentArea areaLoading, areaContent, areaEdit;
    LinearLayout layoutBackups, buttonUpdate;
    TextView buttonUpdateText;
    PreferenceManager preferenceManager;
    TileLarge selectionChangelog, selectionVersionName;
    TileLarge selectionBackup;
    OverridesManager overridesManager;
    InstafelDialog instafelDialogMain;
    JSONObject backup;
    String defaultBackupFile = "Click for select new backup file";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        GeneralFn.updateIflUi(this);
        Localizator.updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_admin_action_updatebackup);
        this.overridesManager = new OverridesManager(this);
        this.preferenceManager = new PreferenceManager(this);
        this.areaLoading = findViewById(R.id.ifl_loading_page);
        this.areaContent = findViewById(R.id.ifl_page_area_backup);
        this.areaEdit = findViewById(R.id.ifl_page_area_edit);
        this.layoutBackups = findViewById(R.id.ifl_backups_layout);
        this.selectionBackup = findViewById(R.id.ifl_tile_selectbackupfile);
        this.selectionChangelog = findViewById(R.id.ifl_tile_setchangelog);
        this.selectionVersionName = findViewById(R.id.ifl_tile_setversionname);
        this.buttonUpdate = findViewById(R.id.ifl_button_updatebackup);
        this.buttonUpdateText = findViewById(R.id.ifl_text_button);

        new ApiGetAdmin(
                this,
                19,
                this.preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_username, "null"),
                this.preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_password, "null")).execute(GeneralFn.getApiUrl(this) + "/admin/user/list_user_backups");
    }

    @Override
    public void getResponse(InstafelResponse instafelResponse, int taskId) {
        if (taskId == 19) {
            if (instafelResponse != null) {
                try {
                    if (instafelResponse.getStatus().equals("SUCCESS")) {
                        JSONObject extras = instafelResponse.getExtra();
                        JSONArray userBackups = extras.getJSONArray("listed_backups");
                        List<BackupListItem> backupListItems = new ArrayList<>();
                        for (int i = 0; i < userBackups.length(); i++) {
                            JSONObject backup = userBackups.getJSONObject(i);
                            backupListItems.add(
                                    new BackupListItem(
                                            backup.getString("id"),
                                            backup.getString("name"),
                                            backup.getString("author")
                                    )
                            );
                        }

                        listItems(backupListItems);
                    } else if (instafelResponse.getStatus().equals("INSUFFICIENT_AUTHORITY")) {
                        Toast.makeText(this, "You don't have UPDATE_BACKUP permission", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Unknown status, " + instafelResponse.getStatus(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error while getting backups from API", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    finish();
                }
            } else {
                Toast.makeText(this, "Error while getting backups from API", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (taskId == 17) {
            if (instafelResponse != null) {
                if (instafelResponse.getStatus().equals("SUCCESS")) {
                    InstafelDialog instafelDialog = InstafelDialog.createSimpleDialog(ifl_a_admin_action_updatebackup.this,
                            "Request Send",
                            "Request send to Instafel API, you can check TG Admin group for more details.",
                            "Okay",
                            null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            },
                            null
                    );
                    instafelDialogMain.hide();
                    instafelDialog.show();
                } else {
                    Toast.makeText(this, "Error: " + instafelResponse.getStatus(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Error while sending request to API", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void listItems(List<BackupListItem> backupListItems) {
        layoutBackups.removeAllViews();
        for (int i = 0; i < backupListItems.size(); i++) {
            BackupListItem backupList = backupListItems.get(i);

            TileCompact tileCompact = new TileCompact(this);
            tileCompact.setIconRes(R.drawable.ifl_backup);
            tileCompact.setTitleText(backupList.getId());
            tileCompact.setOnClickListener(view -> triggerEditPage(backupList));
            layoutBackups.addView(tileCompact);
        }

        areaLoading.setVisibility(View.GONE);
        areaContent.setVisibility(View.VISIBLE);
    }

    public void triggerEditPage(BackupListItem backupList) {
        PageTitle pageTitle = findViewById(R.id.ifl_page_title);
        pageTitle.setText("Edit Backup");
        areaContent.setVisibility(View.GONE);
        areaEdit.setVisibility(View.VISIBLE);
        buttonUpdate.setVisibility(View.VISIBLE);
        buttonUpdateText.setText("Update " + backupList.getId());

        selectionBackup.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            startActivityForResult(intent, 15);
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            boolean requestLimit = false;

            @Override
            public void onClick(View view) {
                try {
                   if (!requestLimit) {
                       if (!selectionBackup.getSubtitle().equals(defaultBackupFile)) {
                           JSONObject requestBody = new JSONObject(backup.toString());
                           requestBody.getJSONObject("info").put("id", backupList.getId());
                           requestBody.getJSONObject("info").put("name", JSONObject.NULL);
                           requestBody.getJSONObject("info").put("author", JSONObject.NULL);

                           ApiPostAdmin apiPostAdmin = new ApiPostAdmin(ifl_a_admin_action_updatebackup.this, 17, preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_username, "def"), preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_password, "def"), requestBody);
                           apiPostAdmin.execute(GeneralFn.getApiUrl(ifl_a_admin_action_updatebackup.this) + "/admin/user/update_backup");
                           instafelDialogMain = new InstafelDialog(ifl_a_admin_action_updatebackup.this);
                           instafelDialogMain.addSpace("top_space", 25);
                           LoadingBar loadingBar = new LoadingBar(ifl_a_admin_action_updatebackup.this);
                           ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
                           int i = (int) ((25 * ifl_a_admin_action_updatebackup.this.getResources().getDisplayMetrics().density) + 0.5f);
                           marginLayoutParams.setMargins(i, 0, i, 0);
                           loadingBar.setLayoutParams(marginLayoutParams);
                           instafelDialogMain.addCustomView("loading_bar", loadingBar);
                           instafelDialogMain.addSpace("button_top_space", 25);
                           instafelDialogMain.show();
                       } else {
                           Toast.makeText(ifl_a_admin_action_updatebackup.this, "Please select backup file", Toast.LENGTH_SHORT).show();
                       }
                   } else {
                       Toast.makeText(ifl_a_admin_action_updatebackup.this, "Request already send", Toast.LENGTH_SHORT).show();
                   }
                } catch (Exception e) {
                    Toast.makeText(ifl_a_admin_action_updatebackup.this, "Error while sending request", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 15:
                Uri backup_uri;
                if (intent != null) {
                    backup_uri = intent.getData();
                } else {
                    backup_uri = null;
                }

                if (backup_uri == null) {
                    Toast.makeText(this, "Please select a backup file for upload", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject contentBackup = overridesManager.readBackupFile(backup_uri);
                    if (contentBackup != null) {
                        backup = contentBackup;
                        selectionBackup.setSubtitleText("Selected");
                        selectionVersionName.setSubtitleText(contentBackup.getJSONObject("info").getString("version"));
                        selectionChangelog.setSubtitleText(contentBackup.getJSONObject("info").getString("changelog"));
                    } else {
                        Toast.makeText(this, "Error while reading backup file", Toast.LENGTH_SHORT).show();
                        selectionBackup.setSubtitleText(defaultBackupFile);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error while selecting backup file", Toast.LENGTH_SHORT).show();
                    selectionBackup.setSubtitleText(defaultBackupFile);
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void getResponse(String rawResponse, int taskId) {

    }
}