package instafel.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import instafel.app.R;

public class Page extends ConstraintLayout {

    public Page(Context ctx) {
        super(ctx);
        init(ctx);
    }

    public Page(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx);
    }

    public Page(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        init(ctx);
    }

    public void init(Context ctx) {
        View rootView = LayoutInflater.from(ctx).inflate(R.layout.ifl_ui_page, this, true);
    }
}
