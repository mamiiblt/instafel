package me.mamiiblt.instafel.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.AttributeManager;

public class TileLargeSwitch extends CardView {

    private ImageView iconView;
    private Switch switchView;
    private TextView titleView, subtitleView;
    private Space spaceTop, spaceBottom;

    public TileLargeSwitch(Context ctx) {
        super(ctx);
        init(ctx, null);
    }

    public TileLargeSwitch(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
        init(ctx, attrSet);
    }

    private void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.ifl_ui_tilelarge_switch, this);
        iconView = findViewById(R.id.ifl_ui_icon);
        titleView = findViewById(R.id.ifl_ui_title);
        subtitleView = findViewById(R.id.ifl_ui_subtitle);
        spaceTop = findViewById(R.id.ifl_ui_space_top);
        spaceBottom = findViewById(R.id.ifl_ui_space_bottom);
        switchView = findViewById(R.id.ifl_ui_switch);
        if (attrs != null) {
            AttributeManager attrManager = new AttributeManager(ctx, attrs);
            setTitleText(attrManager.getString(AttributeManager.ifl_attr_ui_titleText));
            setSubtitleText(attrManager.getString(AttributeManager.ifl_attr_ui_subtitleText));
            setIconRes(attrManager.getResourceId(AttributeManager.ifl_attr_ui_iconRes, R.drawable.ifl_android));
            setSpaceTop(attrManager.getString(AttributeManager.ifl_attr_ui_spaceTop));
            setSpaceBottom(attrManager.getString(AttributeManager.ifl_attr_ui_spaceBottom));
            setIconTint(attrManager.getBoolean(AttributeManager.ifl_attr_ui_iconResTint, true));
            setIconPadding(attrManager.getInteger(AttributeManager.ifl_attr_ui_iconPadding, 4));
            attrManager.recycleTypedArray();
        }
    }

    public Switch getSwitchView() {
        return switchView;
    }

    public void setIconPadding(int dpNum) {
        int paddingDp = dpNum;
        float density = getResources().getDisplayMetrics().density;
        int paddingPixel = (int)(paddingDp * density);
        iconView.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);
    }

    private void setIconTint(boolean value) {
        if (!value) {
            iconView.getDrawable().setTintList(null);
        }
    }

    public void setTitleText(String value) {
        if (value != null) {
            titleView.setText(value);
        } else {
            titleView.setText("Title");
        }

    }

    public void setSubtitleText(String value) {
        if (value != null) {
            subtitleView.setText(value);
        } else {
            subtitleView.setText("Subtitle");
        }
    }

    public void setIconRes(int resId) {
        iconView.setImageResource(resId);
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
