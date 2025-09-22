package instafel.app.activity.devmode.analyzer;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import instafel.app.R;
import instafel.app.managers.OverridesManager;
import instafel.app.managers.modals.FlagItem;
import instafel.app.ui.PageContentArea;
import instafel.app.ui.TileLarge;
import instafel.app.ui.TileTitle;

public class ifl_a_devmode_backup_analyzer extends AppCompatActivity {

    private OverridesManager overridesManager;
    private PageContentArea layout;
    private LinearLayout linearLayout;
    private ConstraintLayout searchLayout;
    private JSONArray mappingContent;
    private EditText editText;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private JSONObject mappingObjects;
    private JSONObject overrideContent;
    private List<FlagItem> flagsFounded = new ArrayList<FlagItem>();
    private List<String> flagsNotFounded = new ArrayList<String>();
    private int totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_devmode_backup_analyzer);

        this.overridesManager = new OverridesManager(this);
        this.layout = findViewById(R.id.ifl_page_area);
        this.linearLayout = findViewById(R.id.ifl_flags_layout);
        this.searchLayout = findViewById(R.id.ifl_search_layout);

        this.editText = findViewById(R.id.ifl_flag_editText);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                search(editText.getText().toString());
            }
            return false;
        });
    }

    private boolean isResumed = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (!isResumed) {
            isResumed = true;

            executor.execute(() -> {
                Set<String> setBackup = new HashSet<>();
                int successTasks = 0;
                String errorString = null;
                try {
                    if (overridesManager.existsMappingFile()) {
                        try {
                            Intent intent = getIntent();
                            overrideContent = new JSONObject(intent.getStringExtra("data"));

                            JSONObject dataObject = new JSONObject(intent.getStringExtra("data"));
                            Uri intentMappingUri = null;
                            if (!dataObject.getString("mapping").equals("NOT_ANY_MAPPING_SELECTED")) {
                                intentMappingUri = Uri.parse(dataObject.getString("mapping"));
                            }

                            JSONObject intentOverrideObject = new JSONObject(dataObject.getString("override"));

                            if (intentOverrideObject.length() != 0) {
                                overrideContent = intentOverrideObject;
                            } else {
                                overrideContent = overridesManager.readOverrideFile();
                            }

                            if (intentMappingUri != null) {
                                mappingContent = overridesManager.readMappingFileFromUri(intentMappingUri);
                            } else {
                                mappingContent = overridesManager.readMappingFile();
                            }

                            if (mappingContent != null) {
                                mappingObjects = overridesManager.parseMappingFile(mappingContent);
                                if (mappingObjects != null) {
                                    Iterator<String> keysBackup = overrideContent.keys();
                                    while (keysBackup.hasNext()) {
                                        setBackup.add(keysBackup.next());
                                    }

                                    for (String key : setBackup) {
                                        String newKey = key.substring(0, key.length() - 1);
                                        if (mappingObjects.has(newKey)) {
                                            JSONObject mapObject = mappingObjects.getJSONObject(newKey);
                                            JSONObject subs = mapObject.getJSONObject("subs");
                                            JSONArray backupValues = overridesManager.getOverrideKeyValues(overrideContent.get(key).toString());
                                            if (backupValues != null) {
                                                String description = "";
                                                for (int i = 0; i < backupValues.length(); i++) {
                                                    JSONArray backupSubValue = backupValues.getJSONArray(i);

                                                    if (i + 1 != backupValues.length()) {
                                                        if (subs.has(backupSubValue.getString(0))) {
                                                            description = description + subs.getString(backupSubValue.getString(0)) + " = " + backupSubValue.getString(1) + "\n";
                                                        } else {
                                                            description = description + "unknown (k" + backupValues.getString(0) + ")" + " = " + backupSubValue.getString(1) + "\n";
                                                        }
                                                    } else {
                                                        if (subs.has(backupSubValue.getString(0))) {
                                                            description = description + subs.getString(backupSubValue.getString(0)) + " = " + backupSubValue.getString(1);
                                                        } else {
                                                            description = description + "unknown (k" + backupValues.getString(0) + ")" + " = " + backupSubValue.getString(1);
                                                        }
                                                    }
                                                }

                                                flagsFounded.add(
                                                        new FlagItem(
                                                                newKey,
                                                                mapObject.getString("name"),
                                                                description
                                                        )
                                                );
                                            } else {
                                                successTasks = -1;
                                                errorString = ifl_a_devmode_backup_analyzer.this.getString(R.string.ifl_a11_30);
                                            }
                                        } else {
                                            if (!newKey.equals("_qe_overrides")) {
                                                flagsNotFounded.add(newKey);
                                            }
                                        }
                                    }

                                    totalSize = setBackup.size();
                                    buildLayout(flagsFounded, flagsNotFounded, null);

                                    successTasks = 1;
                                } else {
                                    successTasks = -1;
                                    errorString = ifl_a_devmode_backup_analyzer.this.getString(R.string.ifl_a11_31);
                                }
                            } else {
                                successTasks = -1;
                                errorString = ifl_a_devmode_backup_analyzer.this.getString(R.string.ifl_a11_32);
                            }
                        } catch (Exception e) {
                            successTasks = -1;
                            errorString = ifl_a_devmode_backup_analyzer.this.getString(R.string.ifl_a11_33);
                            e.printStackTrace();
                        }
                    } else {
                        successTasks = -1;
                        errorString = ifl_a_devmode_backup_analyzer.this.getString(R.string.ifl_a11_34);
                    }
                } catch (Exception e) {
                    successTasks = -1;
                    errorString = ifl_a_devmode_backup_analyzer.this.getString(R.string.ifl_a11_35);
                    e.printStackTrace();
                }

                int finalSuccessTasks = successTasks;
                String finalErrorString = errorString;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalSuccessTasks == -1) {
                            Toast.makeText(ifl_a_devmode_backup_analyzer.this, finalErrorString, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            });
        }
    }

    private void buildLayout(List<FlagItem> flagsFounded, List<String> flagsNotFounded, String searchText) {
        layout.setVisibility(View.GONE);
        new Thread(() -> {
            final List<TileLarge> flagViews = new ArrayList<>();

            TileTitle matchedTileTitle = new TileTitle(ifl_a_devmode_backup_analyzer.this);
            matchedTileTitle.setText(ifl_a_devmode_backup_analyzer.this.getString(R.string.ifl_a4_23));
            matchedTileTitle.setTopPadding(false);
            matchedTileTitle.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            if (searchText == null) {
                for (int i = 0; i < flagsFounded.size(); i++) {
                    FlagItem flagItem = flagsFounded.get(i);
                    flagViews.add(createTile(flagItem.getName(), flagItem.getDesc(), flagItem.getId()));
                }
                matchedTileTitle.setText(matchedTileTitle.getText().toString() + " (" + flagsFounded.size() + "/" + totalSize + ")");
            } else {
                int foundCount = 0;
                for (int i = 0; i < flagsFounded.size(); i++) {
                    FlagItem flagItem = flagsFounded.get(i);
                    if (flagItem.getName().contains(searchText)) {
                        foundCount++;
                        flagViews.add(createTile(flagItem.getName(), flagItem.getDesc(), flagItem.getId()));
                    }
                }
                matchedTileTitle.setText(matchedTileTitle.getText().toString() + " (" + foundCount + "/" + totalSize + ")");
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                linearLayout.removeAllViews();
                linearLayout.addView(matchedTileTitle);
                for (TileLarge flagView : flagViews) {
                    linearLayout.addView(flagView);
                }
                String desc = "";
                for (int i = 0; i < flagsNotFounded.size(); i++) {
                    String id = flagsNotFounded.get(i);
                    if (i + 1 != flagsNotFounded.size()) {
                        desc = desc + id + ",";
                    } else {
                        desc = desc + id;
                    }
                }
                TileLarge mismatchedFlags = createTile(ifl_a_devmode_backup_analyzer.this.getString(R.string.ifl_a4_24) + " (" + flagsNotFounded.size() + "/" + totalSize + ")", desc, null);
                linearLayout.addView(mismatchedFlags);
                linearLayout.setVisibility(View.VISIBLE);

                layout.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private TileLarge createTile(String flag_name, String flag_options, String flag_id) {
        TileLarge tileLarge = new TileLarge(this);
        tileLarge.setTitleText(flag_name);
        tileLarge.setSubtitleText(flag_options);
        tileLarge.setIconVisibility("gone");
        tileLarge.setVisiblitySubIcon("gone");
        tileLarge.setSpaceBottom("visible");
        tileLarge.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tileLarge.setOnClickListener(view -> {
            if (flag_id != null) {
                copyFlagContent(flag_id);
            }
        });
        return tileLarge;
    }

    private void copyFlagContent(String flag_id) {
        try {
            String string = overrideContent.getJSONArray(flag_id + ":").toString();
            JSONObject flag = new JSONObject();
            flag.put(flag_id + ":", string);
            String parsedString = flag.toString();

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("ifl_log_clip", parsedString);
            clipboard.setPrimaryClip(clip);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, this.getString(R.string.ifl_a11_36), Toast.LENGTH_SHORT).show();
        }
    }

    private void search(String editTextContent) {
        if (!editTextContent.isEmpty()) {
            buildLayout(flagsFounded, flagsNotFounded, editTextContent);
        } else {
            buildLayout(flagsFounded, flagsNotFounded, null);
        }
    }
}