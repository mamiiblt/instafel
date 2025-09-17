package me.mamiiblt.instafel.activity.about;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.activity.library.backup.ifl_a_library_backup;
import me.mamiiblt.instafel.activity.library.backup.ifl_a_library_backup_info;
import me.mamiiblt.instafel.ui.PageContentArea;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.ui.TileTitle;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;
import me.mamiiblt.instafel.utils.dialog.InstafelDialogMargins;
import me.mamiiblt.instafel.utils.dialog.InstafelDialogTextType;
import me.mamiiblt.instafel.utils.localization.LocalizedStringGetter;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static me.mamiiblt.instafel.utils.GeneralFn.openInWebBrowser;
import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.localization.LocalizationUtils.updateIflLocale;

public class ifl_a_patches_info  extends AppCompatActivity {

    LinearLayout pageContents;
    JSONArray singlePatches, groupPatches;
    JSONObject appliedPatchCounts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_applied_patches_info);

        pageContents = findViewById(R.id.ifl_patches_layout);
        parseInfoJSON();

        try {
            pageContents.addView(generateTitleTile("Single Patches (" + getPatchCount("singles") + ")"));
            for (int i = 0 ; i < singlePatches.length(); i++) {
                PatchInfo patchInfo = new PatchInfo(singlePatches.getJSONObject(i));
                TileLarge patchTile = generateSinglePatchTile(patchInfo, null);
                if (i + 1 == singlePatches.length()) {
                    patchTile.setSpaceBottom("gone");
                    pageContents.addView(patchTile);
                } else {
                    pageContents.addView(patchTile);
                }
            }

            for (int i = 0; i < groupPatches.length(); i++) {
                GroupInfo groupInfo = new GroupInfo(groupPatches.getJSONObject(i));
                pageContents.addView(generateTitleTile(groupInfo.name + " (" + getPatchCount(groupInfo.shortname) + ")"));

                for (int a = 0; a < groupInfo.patches.size(); a++) {
                    PatchInfo patchInfo = groupInfo.patches.get(a);
                    TileLarge patchTile = generateSinglePatchTile(patchInfo, groupInfo);
                    if (a + 1 ==  groupInfo.patches.size()) {
                        patchTile.setSpaceBottom("gone");
                        pageContents.addView(patchTile);
                    } else {
                        pageContents.addView(patchTile);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occurred while binding patches", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private TileLarge generateSinglePatchTile(PatchInfo patchInfo, GroupInfo groupInfo) {
        TileLarge backupTile = new TileLarge(this);
        backupTile.setTitleText(patchInfo.name);
        backupTile.setSubtitleText(patchInfo.desc);
        backupTile.setIconRes(R.drawable.ifl_patch);
        backupTile.setVisiblitySubIcon("gone");
        backupTile.setSpaceBottom("visible");
        backupTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showInfoDialog(patchInfo, groupInfo);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return backupTile;
    }

    private void showInfoDialog(PatchInfo patchInfo, GroupInfo groupInfo) throws JSONException {
        String desc =
                "• Name: " + patchInfo.name + "\n" +
                "• Shortname: " + patchInfo.shortname + "\n" +
                "• Desc: " + patchInfo.desc + "\n" +
                "• Tasks: " + "\n";

        for (int i = 0; i < patchInfo.tasks.length(); i++) {
            String task = patchInfo.tasks.getString(i);
            desc = desc + "   - " + task + "\n";
        }

        if (groupInfo != null) {
            desc = "About Patch\n" + desc + "\n";
            desc = desc + "About Group\n" +
                    "• Name: " + groupInfo.name + "\n" +
                    "• Shortname: " + groupInfo.shortname + "\n" +
                    "• Desc: " + groupInfo.desc;
        }

        InstafelDialog instafelDialog = new InstafelDialog(this);
        instafelDialog.addSpace("top_space", 25);
        instafelDialog.addTextView(
                "dialog_title",
                "About Patch",
                30,
                0,
                InstafelDialogTextType.TITLE,
                new InstafelDialogMargins(this, 0, 0));
        instafelDialog.addSpace("mid_space", 20);
        instafelDialog.addTextView(
                "dialog_desc_left",
                desc,
                16,
                310,
                InstafelDialogTextType.DESCRIPTION,
                new InstafelDialogMargins(this, 24, 24));
        instafelDialog.addSpace("button_top_space", 20);
        instafelDialog.addPozitiveAndNegativeButton(
                "buttons",
                "Okay",
                "Source",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instafelDialog.dismiss();

                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instafelDialog.dismiss();
                        String patchURL = patchInfo.path.replaceAll("\\.", "/") + ".kt";
                        openInWebBrowser(ifl_a_patches_info.this,
                        "https://github.com/mamiiblt/instafel/blob/dev/patcher-core/src/main/kotlin/instafel/patcher/core/" + patchURL);
                    }
                });
        instafelDialog.addSpace("bottom_space", 27);
        instafelDialog.show();
    }

    private Integer getPatchCount(String groupName) {
        try {
            return appliedPatchCounts.getInt(groupName);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private TileTitle generateTitleTile(String title) {
        TileTitle tileTitle = new TileTitle(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tileTitle.setLayoutParams(params);
        tileTitle.setText(title);
        return tileTitle;
    }

    private void parseInfoJSON() {
        try {
            JSONObject json = new JSONObject(InstafelEnv.APPLIED_PATCHES);
            singlePatches = json.getJSONArray("singlePatches");
            groupPatches = json.getJSONArray("groupPatches");
            appliedPatchCounts = json.getJSONObject("appliedPatchCounts");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occurred while loading patch infos", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    class GroupInfo {
        public String name, desc, shortname, path;
        public ArrayList<PatchInfo> patches;

        public GroupInfo(JSONObject groupObj) {
            try {
                this.name = groupObj.getString("name");
                this.desc = groupObj.getString("desc");
                this.shortname = groupObj.getString("shortname");
                this.path = groupObj.getString("path");

                JSONArray rawPatches = groupObj.getJSONArray("patches");
                this.patches = new ArrayList<>();
                for (int i = 0; i < rawPatches.length(); i++) {
                    patches.add(new PatchInfo(rawPatches.getJSONObject(i)));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ifl_a_patches_info.this, "An error occurred while parsing groupObj", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class PatchInfo {
        public String name, desc, shortname, path, groupShortname;
        public JSONArray tasks;
        public Boolean isSingle;

        public PatchInfo(JSONObject patchObj) {
            try {
                this.name = patchObj.getString("name");
                this.desc = patchObj.getString("desc");
                this.shortname = patchObj.getString("shortname");
                this.path = patchObj.getString("path");
                this.groupShortname = patchObj.getString("groupShortname");
                this.tasks = patchObj.getJSONArray("tasks");
                this.isSingle = patchObj.getBoolean("isSingle");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ifl_a_patches_info.this, "An error occurred while parsing patchObj", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
