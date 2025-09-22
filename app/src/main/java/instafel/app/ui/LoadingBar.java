package instafel.app.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import instafel.app.R;

public class LoadingBar extends ProgressBar {
    public LoadingBar(Context context) {
        super(context);
        init(context);
    }

    public LoadingBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public LoadingBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        applyStyle(context);
        setIndeterminate(true);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.ifl_attr_subtitle_color, typedValue, true);
        setIndeterminateTintList(context.getResources().getColorStateList(typedValue.resourceId, context.getTheme()));
    }

    private void applyStyle(Context context) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(R.style.ifl_theme_dark, new int[]{android.R.attr.indeterminateTint, android.R.attr.indeterminate});
        try {
            ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(0);
            if (colorStateList != null) {
                setIndeterminateTintList(colorStateList);
            }
            setIndeterminate(obtainStyledAttributes.getBoolean(1, true));
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

}
