package me.mamiiblt.instafel.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.AttributeManager;

public class TileLargeEditText extends CardView {

    private ImageView iconView, subIconView;
    private TextView titleView;
    private EditText editTextView;
    private Space spaceTop, spaceBottom;

    public TileLargeEditText(Context ctx) {
        super(ctx);
        init(ctx, null);
    }

    public TileLargeEditText(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
        init(ctx, attrSet);
    }

    private void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.ifl_ui_tilelarge_edittext, this);

        iconView = findViewById(R.id.ifl_ui_icon);
        titleView = findViewById(R.id.ifl_ui_title);
        spaceTop = findViewById(R.id.ifl_ui_space_top);
        spaceBottom = findViewById(R.id.ifl_ui_space_bottom);
        subIconView = findViewById(R.id.ifl_ui_subicon);
        editTextView = findViewById(R.id.ifl_edittext);

        if (attrs != null) {
            AttributeManager attrManager = new AttributeManager(ctx, attrs);
            setTitleText(attrManager.getString(AttributeManager.ifl_attr_ui_titleText));
            setEditTextHint(attrManager.getString(AttributeManager.ifl_attr_ui_subtitleText));
            setIconRes(attrManager.getResourceId(AttributeManager.ifl_attr_ui_iconRes, R.drawable.ifl_android));
            setSpaceTop(attrManager.getString(AttributeManager.ifl_attr_ui_spaceTop));
            setSpaceBottom(attrManager.getString(AttributeManager.ifl_attr_ui_spaceBottom));
            setVisiblitySubIcon(attrManager.getString(AttributeManager.ifl_attr_ui_enableSubIcon));
            setSubIconRes(attrManager.getResourceId(AttributeManager.ifl_attr_ui_subIconRes, R.drawable.ifl_android));
            setIconTint(attrManager.getBoolean(AttributeManager.ifl_attr_ui_iconResTint, true));
            setIconPadding(attrManager.getInteger(AttributeManager.ifl_attr_ui_iconPadding, 4));
            setIconVisibility(attrManager.getString(AttributeManager.ifl_attr_ui_enableIcon));
            attrManager.recycleTypedArray();
        }
    }

    public String getTitle() {
        return titleView.getText().toString();
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

    public void setIconVisibility(String value) {
        if (value != null){
            switch (value) {
                case "gone":
                    iconView.setVisibility(View.GONE);
                    break;
                case "visible":
                    iconView.setVisibility(View.VISIBLE);
                    break;
                case "invisible":
                    iconView.setVisibility(View.INVISIBLE);
                    break;
            }
        } else {
            iconView.setVisibility(View.VISIBLE);
        }
    }

    public void setVisiblitySubIcon(String value) {
        if (value != null){
            switch (value) {
                case "gone":
                    subIconView.setVisibility(View.GONE);
                    break;
                case "visible":
                    subIconView.setVisibility(View.VISIBLE);
                    break;
                case "invisible":
                    subIconView.setVisibility(View.INVISIBLE);
                    break;
            }
        } else {
            subIconView.setVisibility(View.GONE);
        }
    }

    public void setTitleText(String value) {
        if (value != null) {
            titleView.setText(value);
        } else {
            titleView.setVisibility(View.GONE);
        }

    }

    public EditText getEditTextView() {
        return editTextView;
    }

    public void setEditTextHint(String value) {
        if (value != null) {
            editTextView.setHint(value);
        } else {
            editTextView.setHint("Subtitle");
        }
    }

    public void setIconViewAsBitmap(Bitmap bitmap) {
        iconView.setImageBitmap(bitmap);
    }

    public void setIconRes(int resId) {
        iconView.setImageResource(resId);
    }

    public void setSubIconRes(int resId) {
        subIconView.setImageResource(resId);
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
