package instafel.app.utils.dialog;

import android.widget.EditText;

public class StringInputViews {

    private EditText editText;
    private InstafelDialog instafelDialog;

    public StringInputViews(EditText editText, InstafelDialog instafelDialog) {
        this.editText = editText;
        this.instafelDialog = instafelDialog;
    }

    public EditText getEditText() {
        return editText;
    }

    public InstafelDialog getInstafelDialog() {
        return instafelDialog;
    }
}