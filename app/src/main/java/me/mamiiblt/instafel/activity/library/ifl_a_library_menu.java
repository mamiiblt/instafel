package me.mamiiblt.instafel.activity.library;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.activity.library.backup.ifl_a_library_backup;
import me.mamiiblt.instafel.ui.TileCompact;
import me.mamiiblt.instafel.utils.GeneralFn;

public class ifl_a_library_menu extends AppCompatActivity {

    private TileCompact tileBackup, tileFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_library_menu);

        tileBackup = findViewById(R.id.ifl_tile_backup_library);
        tileFlags = findViewById(R.id.ifl_tile_flag_library);

        tileFlags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFn.openInWebBrowser(ifl_a_library_menu.this, "https://instafel.app/library/flag");
            }
        });

        tileBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFn.startIntent(ifl_a_library_menu.this, ifl_a_library_backup.class);
            }
        });
    }
}