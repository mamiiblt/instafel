package instafel.app.activity.admin;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import instafel.app.R;
import instafel.app.managers.OverridesManager;
import instafel.app.ota.IflEnvironment;
import instafel.app.ui.TileCompact;
import instafel.app.utils.GeneralFn;
import instafel.app.utils.InstafelAdminUser;

public class ifl_a_admin_dashboard extends AppCompatActivity {

    TileCompact tileLogout, tileExportMapping, tileUpdateBackup, tilePreferenceManager, tileApprovePreview;
    OverridesManager overridesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_admin_dashboard);

        overridesManager = new OverridesManager(this);
        tileLogout = findViewById(R.id.ifl_tile_admin_action_logout);
        tileExportMapping = findViewById(R.id.ifl_tile_admin_action_export_mapping);
        tilePreferenceManager = findViewById(R.id.ifl_tile_admin_action_sharedpref_manager);
        tileApprovePreview = findViewById(R.id.ifl_tile_admin_update_approve_preview);

        tileUpdateBackup = findViewById(R.id.ifl_tile_admin_update_update_backup);
        tileUpdateBackup.setOnClickListener(view -> GeneralFn.startIntent(ifl_a_admin_dashboard.this, ifl_a_admin_action_updatebackup.class));
        tileApprovePreview.setOnClickListener(view -> GeneralFn.startIntent(ifl_a_admin_dashboard.this, ifl_a_admin_action_approvepreview.class));
        tileExportMapping.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_TITLE, IflEnvironment.getIgVersion(ifl_a_admin_dashboard.this).replace(".", "d") + ".json");
            intent.setType("application/json");
            startActivityForResult(intent, 15);
        });

        tileLogout.setOnClickListener(view -> {
            if (InstafelAdminUser.isUserLogged(ifl_a_admin_dashboard.this)) {
                InstafelAdminUser.logout(ifl_a_admin_dashboard.this);
                finish();
            } else {
                Toast.makeText(ifl_a_admin_dashboard.this, "User not logged", Toast.LENGTH_SHORT).show();
            }
        });

        tilePreferenceManager.setOnClickListener(view -> GeneralFn.startIntent(ifl_a_admin_dashboard.this, ifl_a_admin_pref_manager.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
                    Toast.makeText(this, R.string.ifl_a4_04, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (resultCode == -1) {
                    try {
                        JSONArray contentOverride = overridesManager.readMappingFile();
                        if (contentOverride == null) {
                            Toast.makeText(this, "Error while reading mapping file.", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean status = overridesManager.writeContentIntoMappingFile(backup_uri, contentOverride);
                            if (status) {
                                Toast.makeText(this, contentOverride.length() + " mapping successfully exported", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error while writing content into mapping file.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error while exporting mapping file", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}