package me.mamiiblt.instafel.activity.library.backup;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.GeneralFn;

public class ifl_a_library_backup_info_author extends AppCompatActivity {

    private TileLarge tileAuthorName, tileAuthorGithub, tileAuthorInstagram, tileAuthorMedium, tileAuthorX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_library_backup_info_author);

        tileAuthorName = findViewById(R.id.ifl_tile_backup_library_author_name);
        tileAuthorGithub = findViewById(R.id.ifl_tile_backup_library_social_github);
        tileAuthorInstagram = findViewById(R.id.ifl_tile_backup_library_social_ig);
        tileAuthorMedium = findViewById(R.id.ifl_tile_backup_library_social_medium);
        tileAuthorX = findViewById(R.id.ifl_tile_backup_library_social_x);

        try {
            Intent intent = getIntent();
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));

            tileAuthorName.setSubtitleText(jsonObject.getString("name"));
            if (jsonObject.has("github")) {
                tileAuthorGithub.setSubtitleText("github.com/" + jsonObject.get("github"));
            } else {
                tileAuthorGithub.setVisibility(View.GONE);
            }

            if (jsonObject.has("medium")) {
                tileAuthorMedium.setSubtitleText("medium.com/@" + jsonObject.get("medium"));
            } else {
                tileAuthorMedium.setVisibility(View.GONE);
            }

            if (jsonObject.has("x")) {
                tileAuthorX.setSubtitleText("x.com/" + jsonObject.get("x"));
            } else {
                tileAuthorX.setVisibility(View.GONE);
            }

            if (jsonObject.has("instagram")) {
                tileAuthorInstagram.setSubtitleText("instagram.com/" + jsonObject.get("instagram"));
            } else {
                tileAuthorInstagram.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, this.getString(R.string.ifl_a11_21), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        tileAuthorGithub.setOnClickListener(view -> openUrl(tileAuthorGithub.getSubtitle()));
        tileAuthorInstagram.setOnClickListener(view -> openUrl(tileAuthorInstagram.getSubtitle()));
        tileAuthorMedium.setOnClickListener(view -> openUrl(tileAuthorMedium.getSubtitle()));
        tileAuthorX.setOnClickListener(view -> openUrl(tileAuthorX.getSubtitle()));
    }

    public void openUrl(String url) {
        GeneralFn.openInWebBrowser(this, "https://" + url);
    }
}