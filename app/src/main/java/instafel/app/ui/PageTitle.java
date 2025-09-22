package instafel.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Space;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import instafel.app.R;
import instafel.app.managers.AttributeManager;


public class PageTitle extends ConstraintLayout {

    private TextView textView;
    private Space spaceBottom;

    public PageTitle(Context ctx) {
        super(ctx);
        init(ctx, null);
    }

    public PageTitle(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx, attrs);
    }

    public void init(Context ctx, AttributeSet attrs) {
        inflate(ctx, R.layout.ifl_ui_pagetitle, this);
        textView = findViewById(R.id.ifl_ui_page_title);
        spaceBottom = findViewById(R.id.ifl_titleSpace);

        if (attrs != null) {
            AttributeManager attrManager = new AttributeManager(ctx, attrs);
            setText(attrManager.getString(AttributeManager.ifl_attr_ui_titleText, "ifl_ui"));
            setSpaceBottom(attrManager.getString(AttributeManager.ifl_attr_ui_spaceBottom, "visible"));
            attrManager.recycleTypedArray();
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

    public void setText(String value) {
        textView.setText(value);
    }
}
