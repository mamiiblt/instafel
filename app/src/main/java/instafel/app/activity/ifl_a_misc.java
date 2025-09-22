package instafel.app.activity;

import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import instafel.app.R;
import instafel.app.InstafelEnv;
import instafel.app.managers.PreferenceManager;
import instafel.app.ui.TileLargeSwitch;
import instafel.app.utils.GeneralFn;

public class ifl_a_misc extends AppCompatActivity {
    PreferenceManager preferenceManager;

    @Override 
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        GeneralFn.updateIflUi(this);
        updateIflLocale(this, false);
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