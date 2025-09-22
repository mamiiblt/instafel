package instafel.app.activity.library.backup;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import instafel.app.R;
import instafel.app.api.models.BackupListItem;
import instafel.app.api.models.InstafelResponse;
import instafel.app.api.requests.ApiCallbackInterface;
import instafel.app.api.requests.ApiGetString;
import instafel.app.InstafelEnv;
import instafel.app.managers.PreferenceManager;
import instafel.app.ui.PageContentArea;
import instafel.app.ui.TileLarge;
import instafel.app.ui.TileLargeSwitch;
import instafel.app.utils.GeneralFn;
import instafel.app.utils.types.PreferenceKeys;

public class ifl_a_library_backup extends AppCompatActivity implements ApiCallbackInterface {

    private ConstraintLayout layoutSearch;
    private PageContentArea layoutLoading;
    private PageContentArea layoutContent;
    private LinearLayout layoutBackups;
    private EditText editText;
    private TileLargeSwitch tileAutoUpdate;
    private Switch tileAutoUpdateSwitch;
    private PreferenceManager preferenceManager;
    private boolean isUserChangedStatus = false;
    private List<BackupListItem> backups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_library_backup);

        preferenceManager = new PreferenceManager(this);
        tileAutoUpdate = findViewById(R.id.ifl_tile_backup_library_enableAutoUpdates);
        tileAutoUpdateSwitch = tileAutoUpdate.getSwitchView();
        layoutSearch = findViewById(R.id.ifl_search_layout);
        layoutLoading = findViewById(R.id.ifl_loading_page);
        layoutBackups = findViewById(R.id.ifl_backups_layout);
        layoutContent = findViewById(R.id.ifl_page_area_backup);
        editText = findViewById(R.id.ifl_backup_editText);

        if (preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_enable_auto_update, false)) {
            tileAutoUpdateSwitch.setChecked(true);
        } else {
            tileAutoUpdateSwitch.setChecked(false);
        }

        tileAutoUpdateSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (InstafelEnv.PRODUCTION_MODE) {
                if (!isUserChangedStatus) {
                    setAutoUpdateState(b);
                }
            } else {
                Toast.makeText(ifl_a_library_backup.this, "This feauture isn't available on custom generations.", Toast.LENGTH_SHORT).show();
            }
        });

        tileAutoUpdate.setOnClickListener(view -> {
            if (InstafelEnv.PRODUCTION_MODE) {
                isUserChangedStatus = true;
                setAutoUpdateState(!tileAutoUpdateSwitch.isChecked());
                isUserChangedStatus = false;
            } else {
                Toast.makeText(ifl_a_library_backup.this, "This feauture isn't available on custom generations.", Toast.LENGTH_SHORT).show();
            }
        });


        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String searchText = editText.getText().toString();
                if (!searchText.isEmpty()) {
                    buildLayout(backups, searchText);
                } else {
                    buildLayout(backups, null);
                }
            }
            return false;
        });

        ApiGetString apiGetString = new ApiGetString(this, 11);
        apiGetString.execute("https://raw.githubusercontent.com/instafel/backups/main/backups.json");
    }

    public void setAutoUpdateState(boolean newState) {
        if (newState) {
            String backupUpdateValue = preferenceManager.getPreferenceString(PreferenceKeys.ifl_backup_update_value, "def");
            if (!backupUpdateValue.equals("def") && !backupUpdateValue.equals("CANNOT_BE_CONVERTED")) {
                try {
                    JSONObject lastAppliedBackupInfo = new JSONObject(backupUpdateValue);
                    Toast.makeText(this, this.getString(R.string.ifl_a11_24, lastAppliedBackupInfo.getString("name")), Toast.LENGTH_SHORT).show();
                    preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_enable_auto_update, true);
                    tileAutoUpdateSwitch.setChecked(true);
                } catch (Exception e) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_23), Toast.LENGTH_SHORT).show();
                    preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_enable_auto_update, false);
                    tileAutoUpdateSwitch.setChecked(false);
                }
            } else {
                Toast.makeText(this, this.getString(R.string.ifl_a11_23), Toast.LENGTH_SHORT).show();
                preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_enable_auto_update, false);
                tileAutoUpdateSwitch.setChecked(false);
            }
        } else {
            Toast.makeText(this, this.getString(R.string.ifl_a11_25), Toast.LENGTH_SHORT).show();
            preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_enable_auto_update, false);
            tileAutoUpdateSwitch.setChecked(false);
        }
    }

    @Override
    public void getResponse(String rawResponse, int taskId) {
        if (taskId == 11) {
            if (rawResponse != null) {
                try {
                    JSONObject response = new JSONObject(rawResponse);
                    JSONArray extraBackups = response.getJSONArray("backups");
                    Log.v("Instafel", extraBackups.toString());
                    for (int i = 0; i < extraBackups.length(); i++) {
                        JSONObject backup = extraBackups.getJSONObject(i);
                        backups.add(
                                new BackupListItem(
                                        backup.getString("id"),
                                        backup.getString("name"),
                                        backup.getString("author")
                                )
                        );
                    }

                    buildLayout(backups, null);
                } catch (Exception e) {
                    Toast.makeText(this, this.getString(R.string.ifl_a11_03), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    finish();
                }
            } else {
                Toast.makeText(this, this.getString(R.string.ifl_a11_03), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void buildLayout(List<BackupListItem> backups, String searchParam) {
        layoutContent.setVisibility(View.GONE);
        layoutBackups.setVisibility(View.GONE);
        layoutSearch.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);

        layoutBackups.removeAllViews();
        if (searchParam == null) {
            for (int i = 0; i < backups.size(); i++) {
                layoutBackups.addView(createBackupTile(backups.get(i)));
            }
        } else {
            for (int i = 0; i < backups.size(); i++) {
                BackupListItem backup = backups.get(i);
                if (backup.getName().toLowerCase().contains(searchParam.toLowerCase())) {
                    layoutBackups.addView(createBackupTile(backup));
                }
            }
        }

        layoutContent.setVisibility(View.VISIBLE);
        layoutBackups.setVisibility(View.VISIBLE);
        layoutSearch.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
    }

    public TileLarge createBackupTile(BackupListItem backup) {
        TileLarge backupTile = new TileLarge(this);
        backupTile.setTitleText(backup.getName());
        backupTile.setSubtitleText(this.getString(R.string.ifl_a11_04, backup.getAuthor()));
        backupTile.setIconRes(R.drawable.ifl_backup);
        backupTile.setVisiblitySubIcon("gone");
        backupTile.setSpaceBottom("visible");
        backupTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFn.startIntentWithString(ifl_a_library_backup.this, ifl_a_library_backup_info.class, backup.convertForPutIntoActivity());
            }
        });
        return backupTile;
    }

    public void showExample(View view) {
        Toast.makeText(this, preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_enable_auto_update, false) + "s", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getResponse(InstafelResponse instafelResponse, int taskId) {

    }
}