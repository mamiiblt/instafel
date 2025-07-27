package me.mamiiblt.instafel.utils.localization;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.ui.TileLarge;

public class LocalizationInfo {

    private Context ctx;
    public TileLarge localeTile;
    public Locales.LocaleType localeData;

    public LocalizationInfo(Context ctx, Locales.LocaleType localeData) {
        this.ctx = ctx;
        this.localeData = localeData;
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
        localeTile.setTitleText(localeData.langName);
        localeTile.setSubtitleText(localeData.langCountry);
        localeTile.setIconRes(localeData.flagDrawableID);
        localeTile.setIconTint(false);
        localeTile.setSpaceBottom("visible");
        localeTile.setVisiblitySubIcon("gone");
        localeTile.setSubIconRes(R.drawable.ifl_tick);
        return localeTile;
    }
}
