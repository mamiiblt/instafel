package instafel.app.utils.localization;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import instafel.app.R;
import instafel.app.ui.TileLarge;
import instafel.app.utils.GeneralFn;

import java.util.Locale;

public class LocaleInfoTile {

    private Context ctx;
    public TileLarge localeTile;
    public Locale locale;

    public LocaleInfoTile(Context ctx, Locale locale) {
        this.ctx = ctx;
        this.locale = locale;
        this.localeTile = createLangTile();
    }

    public void setTileVisibility(boolean state) {
        localeTile.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    public void setTickStatus(boolean state) {
        localeTile.setVisiblitySubIcon(state ? "visible" : "gone");
    }

    private TileLarge createLangTile() {
        TileLarge localeTile = new TileLarge(ctx);
        localeTile.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        localeTile.setTitleText(GeneralFn.capitalizeFirstLetter(locale.getDisplayLanguage(locale)));
        localeTile.setSubtitleText(GeneralFn.capitalizeFirstLetter(locale.getDisplayCountry(locale)));
        localeTile.setIconRes(R.drawable.ifl_language);
        localeTile.setIconTint(true);
        localeTile.setSpaceBottom("visible");
        localeTile.setVisiblitySubIcon("gone");
        localeTile.setSubIconRes(R.drawable.ifl_tick);
        return localeTile;
    }
}
