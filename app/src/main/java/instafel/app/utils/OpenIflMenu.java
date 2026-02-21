package instafel.app.utils;

appimport android.app.Activity;
import android.content.Context;
import android.view.View;

public class OpenIflMenu implements View.OnLongClickListener {
    private Activity mainAppActivity;

    public OpenIflMenu(Context context) {
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
