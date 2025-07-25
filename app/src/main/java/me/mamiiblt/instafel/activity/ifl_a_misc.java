package me.mamiiblt.instafel.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ui.TileLargeSwitch;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.Localizator;

public class ifl_a_misc extends AppCompatActivity {
    PreferenceManager preferenceManager;

    @Override 
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        GeneralFn.updateIflUi(this);
        Localizator.updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_misc);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        this.preferenceManager = preferenceManager;

        TileLargeSwitch tileRemoveAds = findViewById(R.id.ifl_tile_remove_ads_section);
        Switch tileRemoveAdsSwitch = tileRemoveAds.getSwitchView();
        boolean removedAdsPatchApplied = InstafelEnv.isPatchApplied("remove_ads");
        if (removedAdsPatchApplied) {
            tileRemoveAdsSwitch.setChecked(true);
            tileRemoveAdsSwitch.setEnabled(false);
            tileRemoveAds.setOnClickListener(view -> Toast.makeText(ifl_a_misc.this, ifl_a_misc.this.getString(R.string.ifl_a0_14), Toast.LENGTH_SHORT).show());
        } else {
            tileRemoveAds.setVisibility(View.GONE);
        }
    }
}