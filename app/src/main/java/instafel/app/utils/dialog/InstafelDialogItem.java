/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

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
