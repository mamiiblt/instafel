package instafel.app.activity.about;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.os.Bundle;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import instafel.app.R;
import instafel.app.InstafelEnv;
import instafel.app.ota.IflEnvironment;
import instafel.app.ui.TileLarge;
import instafel.app.utils.GeneralFn;

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
        tilePatcherVersion.setOnClickListener(view -> openUrlInWeb("https://github.com/instafel/p-rel/releases/tag/" + InstafelEnv.PATCHER_VERSION));

        tileAppliedPatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFn.startIntent(ifl_a_build_info.this, ifl_a_patches_info.class);
            }
        });

        tileInstallationType.setSubtitleText(IflEnvironment.getTypeString(this, Locale.getDefault()));
    }

    public void openUrlInWeb(String url) {
        GeneralFn.openInWebBrowser(this, url);
    }
}