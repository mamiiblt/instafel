package instafel.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import instafel.app.R;
import instafel.app.managers.AttributeManager;

public class TileTitle extends CardView {

    private TextView textView;

    public TileTitle(Context ctx) {
        super(ctx);
        init(ctx, null);
    }

    public TileTitle(Context ctx, AttributeSet attrSet) {
        super(ctx, attrSet);
        init(ctx, attrSet);
    }

    public void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.ifl_ui_tiletitle, this);
        textView = findViewById(R.id.ifl_ui_text);
        if (attrs != null) {
            AttributeManager attrManager = new AttributeManager(ctx, attrs);
            setText(attrManager.getString(AttributeManager.ifl_attr_ui_titleText, "ifl_ui"));
            attrManager.recycleTypedArray();
        }
    }

    public void setTopPadding(boolean state) {
        if (!state) {
            textView.setPadding(textView.getPaddingLeft(), 0, textView.getPaddingRight(), textView.getPaddingBottom());
        }
    }

    public String getText() {
        return textView.getText().toString();
    }
    public void setText(String value) {
        textView.setText(value);
    }
}
