package me.mamiiblt.instafel.activity.library;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.activity.library.backup.ifl_a_library_backup;
import me.mamiiblt.instafel.utils.GeneralFn;

public class ifl_a_library_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_library_menu);

        findViewById(R.id.ifl_tile_flag_library).setOnClickListener(view -> GeneralFn.openInWebBrowser(ifl_a_library_menu.this, "https://instafel.app/library/flag"));
        findViewById(R.id.ifl_tile_backup_library).setOnClickListener(view -> GeneralFn.startIntent(ifl_a_library_menu.this, ifl_a_library_backup.class));
    }

}