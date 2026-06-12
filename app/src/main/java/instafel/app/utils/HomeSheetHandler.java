/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.app.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class HomeSheetHandler implements View.OnLongClickListener {
    private Activity mainAppActivity;

    public HomeSheetHandler(Context context) {
        if (context instanceof Activity) {
            this.mainAppActivity = (Activity) context;
        } else {
            this.mainAppActivity = null;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mainAppActivity == null) return false;

        InstafelHomeSheet instafelHomeSheet = new InstafelHomeSheet(mainAppActivity);
        instafelHomeSheet.buildAndShowDialog();
        return true;
    }
}
