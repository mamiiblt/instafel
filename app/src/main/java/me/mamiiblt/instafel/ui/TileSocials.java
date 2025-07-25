package me.mamiiblt.instafel.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Space;

import androidx.cardview.widget.CardView;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.AttributeManager;

public class TileSocials extends CardView  {
    private CardView tileChat, tileLanguage, tileInfo, tileGuide, tileGithub;
    private Space spaceTop, spaceBottom;

    public TileSocials(Context ctx) {
        super(ctx);
        init(ctx, null);
    }

    public TileSocials(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
        init(ctx, attrSet);
    }

    private void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.ifl_ui_tilesocials, this);
        spaceTop = findViewById(R.id.ifl_ui_space_top);
        spaceBottom = findViewById(R.id.ifl_ui_space_bottom);
        tileChat = findViewById(R.id.ifl_tiles_chat);
        tileLanguage = findViewById(R.id.ifl_tiles_language);
        tileInfo = findViewById(R.id.ifl_tiles_about);
        tileGuide = findViewById(R.id.ifl_tiles_guide);
        tileGithub = findViewById(R.id.ifl_tiles_github);
        if (attrs != null) {
            AttributeManager attrManager = new AttributeManager(ctx, attrs);
            setSpaceTop(attrManager.getString(AttributeManager.ifl_attr_ui_spaceTop));
            setSpaceBottom(attrManager.getString(AttributeManager.ifl_attr_ui_spaceBottom));
            attrManager.recycleTypedArray();
        }
    }

    public CardView getTileChat() {
        return tileChat;
    }

    public CardView getTileGithub() {
        return tileGithub;
    }

    public CardView getTileLanguage() {
        return tileLanguage;
    }

    public CardView getTileInfo() {
        return tileInfo;
    }

    public CardView getTileGuide() {
        return tileGuide;
    }

    public void setSpaceTop(String value) {
        if (value != null){
            switch (value) {
                case "gone":
                    spaceTop.setVisibility(View.GONE);
                    break;
                case "visible":
                    spaceTop.setVisibility(View.VISIBLE);
                    break;
                case "invisible":
                    spaceTop.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    public void setSpaceBottom(String value) {
        if (value != null) {
            switch (value) {
                case "gone":
                    spaceBottom.setVisibility(View.GONE);
                    break;
                case "visible":
                    spaceBottom.setVisibility(View.VISIBLE);
                    break;
                case "invisible":
                    spaceBottom.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }
}

