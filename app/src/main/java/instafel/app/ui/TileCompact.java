package instafel.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.cardview.widget.CardView;


import instafel.app.R;
import instafel.app.managers.AttributeManager;

public class TileCompact extends CardView  {
    private ImageView iconView;
    private TextView titleView;
    private Space spaceTop, spaceBottom;

    public TileCompact(Context ctx) {
        super(ctx);
        init(ctx, null);
    }

    public TileCompact(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
        init(ctx, attrSet);
    }

    private void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.ifl_ui_tilecompact, this);
        iconView = findViewById(R.id.ifl_ui_icon);
        titleView = findViewById(R.id.ifl_ui_title);
        spaceTop = findViewById(R.id.ifl_ui_space_top);
        spaceBottom = findViewById(R.id.ifl_ui_space_bottom);
        if (attrs != null) {
            AttributeManager attrManager = new AttributeManager(ctx, attrs);
            setTitleText(attrManager.getString(AttributeManager.ifl_attr_ui_titleText, "ifl_ui"));
            setIconRes(attrManager.getResourceId(AttributeManager.ifl_attr_ui_iconRes, R.drawable.ifl_android));
            setSpaceTop(attrManager.getString(AttributeManager.ifl_attr_ui_spaceTop));
            setSpaceBottom(attrManager.getString(AttributeManager.ifl_attr_ui_spaceBottom));
            setIconPadding(attrManager.getInteger(AttributeManager.ifl_attr_ui_iconPadding, iconView.getPaddingBottom()));
            attrManager.recycleTypedArray();
        }
    }

    public void setTitleText(String value) {
        if (value != null) {
            titleView.setText(value);
        } else {
            titleView.setText("Title");
        }
    }

    public void setIconPadding(int dpNum) {
        int paddingDp = dpNum;
        float density = getResources().getDisplayMetrics().density;
        int paddingPixel = (int)(paddingDp * density);
        iconView.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);
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

