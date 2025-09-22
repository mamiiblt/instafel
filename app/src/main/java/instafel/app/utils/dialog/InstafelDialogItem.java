package instafel.app.utils.dialog;

import android.view.View;

public class InstafelDialogItem {

    private String name;
    private View view;

    public String getName() {
        return name;
    }

    public View getView() {
        return view;
    }

    public InstafelDialogItem(String name, View view) {
        this.name = name;
        this.view = view;
    }
}
