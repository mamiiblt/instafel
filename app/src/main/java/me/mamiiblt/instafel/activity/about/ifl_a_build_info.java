package me.mamiiblt.instafel.activity.about;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.ota.IflEnvironment;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.GeneralFn;

public class ifl_a_build_info extends AppCompatActivity {

    private TileLarge tileGenerationId, tilePatcherVersion, tileAppliedPatches, tileInstallationType, tileCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_build_info);

        tileGenerationId = findViewById(R.id.ifl_tile_generation_id);
        tilePatcherVersion = findViewById(R.id.ifl_tile_patcher_version);
        tileCommit = findViewById(R.id.ifl_tile_commit);
        tileAppliedPatches = findViewById(R.id.ifl_tile_applied_patches);
        tileInstallationType = findViewById(R.id.ifl_tile_installation_type);

        tileGenerationId.setSubtitleText(IflEnvironment.getGenerationId(this));
        tileGenerationId.setOnClickListener(view -> openUrlInWeb("https://instafel.app/download?version=v" + InstafelEnv.IFL_VERSION));

        tilePatcherVersion.setSubtitleText(InstafelEnv.PATCHER_VERSION + " (" + InstafelEnv.PATCHER_TAG + ")");
        tileCommit.setSubtitleText(InstafelEnv.COMMIT + " (main)");

        tileCommit.setOnClickListener(view -> openUrlInWeb("https://github.com/mamiiblt/instafel/commit/" + InstafelEnv.COMMIT));

        String appliedPatches = InstafelEnv.APPLIED_PATCHES.replace(",", "\n");
        tileAppliedPatches.setSubtitleText(appliedPatches);

        tileInstallationType.setSubtitleText(IflEnvironment.getTypeString(this, Locale.getDefault()));
    }

    public void openUrlInWeb(String url) {
        GeneralFn.openInWebBrowser(this, url);
    }
}