package instafel.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import instafel.app.R;

public class PageContentArea extends LinearLayout {
    public PageContentArea(Context ctx) {
        super(ctx);
    }

    public PageContentArea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageContentArea(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final ViewGroup container = inflate(getContext(), R.layout.ifl_ui_pagecontentarea, this)
                .findViewById(R.id.ifl_ui_content_area);
        while (getChildCount() > 1) {
            final View child = getChildAt(0);
            removeView(child);
            container.addView(child, child.getLayoutParams());
        }
    }
}
