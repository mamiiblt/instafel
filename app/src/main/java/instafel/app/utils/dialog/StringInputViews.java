/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

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