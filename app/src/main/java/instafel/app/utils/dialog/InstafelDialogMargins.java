package instafel.app.utils.dialog;

import android.app.Activity;
import android.util.TypedValue;

public class InstafelDialogMargins {
    private int start, end;
    private Activity activity;

    public int getStart() {
        return convert(start);
    }

    public int getEnd() {
        return convert(end);
    }

    private int convert(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, activity.getResources().getDisplayMetrics());
    }

    public InstafelDialogMargins(Activity act, int start, int end) {
        this.activity = act;
        this.start = start;
        this.end = end;
    }
}
