package instafel.app.activity.about;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import instafel.app.R;
import instafel.app.ota.IflEnvironment;
import instafel.app.ui.TileLarge;
import instafel.app.utils.GeneralFn;

public class ifl_a_about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_about);

        TileLarge tileVersion = findViewById(R.id.ifl_tile_ifl_version);
        tileVersion.setSubtitleText(IflEnvironment.getIflVersionString(this));

        TileLarge tileInstagramVersion = findViewById(R.id.ifl_tile_ig_version);
        tileInstagramVersion.setSubtitleText(IflEnvironment.getIgVerAndCodeString(this));
        tileInstagramVersion.setSubtitleText(IflEnvironment.getIgVerAndCodeString(this));
        tileInstagramVersion.setOnClickListener(view -> {
            ((ClipboardManager) ifl_a_about.this.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("ifl_log_clip", tileInstagramVersion.getSubtitle()));
        });

        findViewById(R.id.ifl_tile_build_info).setOnClickListener(view -> GeneralFn.startIntent(ifl_a_about.this, ifl_a_build_info.class));
        findViewById(R.id.ifl_tile_contributors).setOnClickListener(v -> openUrlInWeb("https://instafel.app/contributors"));
        findViewById(R.id.ifl_tile_website).setOnClickListener(v -> openUrlInWeb("https://instafel.app"));
        findViewById(R.id.ifl_tile_about_dev).setOnClickListener(v -> openUrlInWeb("https://mamii.dev/about"));
        findViewById(R.id.ifl_tile_join_community).setOnClickListener(v -> openUrlInWeb("https://t.me/instafel"));
    }

    public void openUrlInWeb(String url) {
        GeneralFn.openInWebBrowser(this, url);
    }
}