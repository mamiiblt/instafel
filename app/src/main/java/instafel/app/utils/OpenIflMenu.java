package instafel.app.utils;

import android.view.View;

public class OpenIflMenu implements View.OnLongClickListener {
    public OpenIflMenu() {
        super();
    }

    @Override
    public boolean onLongClick(View view) {
        InitializeInstafel.startInstafel();
        return true;
    }
}
