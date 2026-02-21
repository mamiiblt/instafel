package instafel.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import instafel.app.InstafelEnv;
import instafel.app.R;
import instafel.app.utils.localization.LocalizationUtils;
import instafel.app.utils.localization.LocalizedStringGetter;

import java.util.Locale;

public class InstafelHomeSheet {

    private Activity act;
    private Dialog sheetDialog = null;
    private int sheetThemeMode;
    private Locale iflLocale;

    public InstafelHomeSheet(Activity act) {
        this.act = act;
        this.sheetThemeMode = GeneralFn.getUiMode(act);
        this.iflLocale = LocalizationUtils.getIflLocale(act);
    }

    public void buildAndShowDialog() {
        try {
            buildSheet();
            sheetDialog.show();
        } catch (Exception e) {
            Toast.makeText(act, "An error occurred while building / showing dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void buildSheet() {
        Dialog dialog = new Dialog(act);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        LinearLayout root = new LinearLayout(act);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));

        GradientDrawable bg = new GradientDrawable();
        if (sheetThemeMode == 0 || sheetThemeMode == 1) {
            bg.setColor(act.getResources().getColor(R.color.ifl_sheet_background_dark));
        } else {
            bg.setColor(act.getResources().getColor(R.color.ifl_sheet_background_light));
        }
        bg.setCornerRadii(new float[]{
                dp(28), dp(28),
                dp(28), dp(28),
                dp(28), dp(28),
                dp(28), dp(28)
        });
        root.setBackground(bg);
        root.addView(buildSheetContent());

        dialog.setContentView(root);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            window.setWindowAnimations(android.R.style.Animation_Dialog);
        }
        this.sheetDialog = dialog;
    }

    private LinearLayout buildSheetContent() {
        int bgColor = 0;
        int mainText = 0;
        int subText = 0;
        if (sheetThemeMode == 0 || sheetThemeMode == 1) {
            bgColor = act.getResources().getColor(R.color.ifl_sheet_background_dark);
            mainText = act.getResources().getColor(R.color.ifl_sheet_text_main_dark);
            subText = act.getResources().getColor(R.color.ifl_sheet_text_sub_dark);
        } else {
            bgColor = act.getResources().getColor(R.color.ifl_sheet_background_light);
            mainText = act.getResources().getColor(R.color.ifl_sheet_text_main_light);
            subText = act.getResources().getColor(R.color.ifl_sheet_text_sub_light);
        }

        LinearLayout root = new LinearLayout(act);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        root.setBackgroundColor(bgColor);

        ConstraintLayout header = new ConstraintLayout(act);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ImageView icon = new ImageView(act);
        icon.setId(View.generateViewId());
        icon.setImageResource(R.drawable.ifl_instafel);
        icon.setColorFilter(mainText);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        ConstraintLayout.LayoutParams iconLp = new ConstraintLayout.LayoutParams(dp(49), dp(49));
        iconLp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        iconLp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        iconLp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        iconLp.setMargins(dp(15), dp(15), 0, dp(15));
        icon.setLayoutParams(iconLp);

        ConstraintLayout textBox = new ConstraintLayout(act);
        textBox.setId(View.generateViewId());

        ConstraintLayout.LayoutParams boxLp = new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        boxLp.startToEnd = icon.getId();
        boxLp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        boxLp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        boxLp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        boxLp.setMargins(dp(15), dp(15), dp(15), dp(15));
        textBox.setLayoutParams(boxLp);

        TextView title = new TextView(act);
        title.setId(View.generateViewId());
        title.setText("Instafel v" + InstafelEnv.IFL_VERSION);
        title.setTextColor(mainText);
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);

        ConstraintLayout.LayoutParams titleLp =
                new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        titleLp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        titleLp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        title.setLayoutParams(titleLp);

        TextView sub = new TextView(act);
        sub.setText(LocalizedStringGetter.getDialogLocalizedString(act, iflLocale, "ifl_d5_01", InstafelEnv.IG_VERSION));
        sub.setTextColor(subText);
        sub.setTextSize(14);

        ConstraintLayout.LayoutParams subLp =
                new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        subLp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        subLp.topToBottom = title.getId();
        subLp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        sub.setLayoutParams(subLp);

        textBox.addView(title);
        textBox.addView(sub);
        header.addView(icon);
        header.addView(textBox);

        root.addView(header);

        root.addView(menuItem(
                R.drawable.ifl_menu,
                LocalizedStringGetter.getDialogLocalizedString(act, iflLocale, "ifl_d5_02"),
                mainText, v -> InitializeInstafel.startInstafel()));

        root.addView(menuItem(
                R.drawable.ifl_sheet_devopts,
                LocalizedStringGetter.getDialogLocalizedString(act, iflLocale, "ifl_d5_03"),
                mainText, v -> openDeveloperOptions()));

        root.addView(menuItem(
                R.drawable.ifl_community_icon,
                LocalizedStringGetter.getDialogLocalizedString(act, iflLocale, "ifl_d5_04"),
                mainText, v -> GeneralFn.openInWebBrowser(act, "https://t.me/instafel")));

        root.addView(menuItem(
                R.drawable.ifl_sheet_reload,
                LocalizedStringGetter.getDialogLocalizedString(act, iflLocale, "ifl_d5_05"),
                mainText, v -> restartInstagram()));

        return root;
    }

    public void restartInstagram() {
        Intent intent = act.getPackageManager().getLaunchIntentForPackage(act.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        act.startActivity(intent);
        Runtime.getRuntime().exit(0);
    }

    public void openDeveloperOptions() {

    }

    private View menuItem(int iconRes, String text, int color, View.OnClickListener onClickListener) {

        LinearLayout item = new LinearLayout(act);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(dp(15), dp(12), dp(15), dp(12));
        item.setClickable(true);
        item.setFocusable(true);
        item.setOnClickListener(onClickListener);

        ImageView icon = new ImageView(act);
        icon.setImageResource(iconRes);
        icon.setColorFilter(color);
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp(24), dp(24)));

        View spacer = new View(act);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(dp(12), 1));

        TextView tv = new TextView(act);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(color);
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        item.addView(icon);
        item.addView(spacer);
        item.addView(tv);

        return item;
    }

    private static int dp(int v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                v,
                Resources.getSystem().getDisplayMetrics()
        );
    }
}
