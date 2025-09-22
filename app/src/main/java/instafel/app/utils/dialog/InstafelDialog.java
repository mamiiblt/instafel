package instafel.app.utils.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import instafel.app.R;
import instafel.app.utils.GeneralFn;

public class InstafelDialog {
    private Activity act;
    private int uiMode = 0;
    private Dialog dialog;
    private CardView dialogRoot;
    private LinearLayout dialogMainArea;
    private List<InstafelDialogItem> dialogItems = new ArrayList<InstafelDialogItem>();

    private int dialogThemeMode;

    public InstafelDialog(Activity ctx) {
        this.act = ctx;
        this.dialog = new Dialog(this.act);
        this.dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.dialog.getWindow().setBackgroundDrawable(act.getDrawable(R.drawable.ifl_dg_ota_background));
        this.dialog.setCancelable(false);
        setBaseLayout();
    }

    public void cancel() {
        dialog.cancel();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void hide() {
        dialog.hide();
    }

    public void show() {
        try {
            addComponentsIntoDialog();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSpace(String componentName, int height) {
        Space space = new Space(act);
        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, act.getResources().getDisplayMetrics())
        );
        space.setLayoutParams(spaceParams);
        dialogItems.add(new InstafelDialogItem(componentName, space));

    }

    public static StringInputViews createSimpleInputDialog_String (Activity activity, String title, boolean editTextMultiline) {
        InstafelDialog instafelDialog = new InstafelDialog(activity);
        instafelDialog.addSpace("top_space", 25);
        instafelDialog.addTextView(
                "dialog_title",
                title,
                30,
                0,
                InstafelDialogTextType.TITLE,
                new InstafelDialogMargins(activity, 0, 0));
        instafelDialog.addSpace("mid_space", 20);
        EditText inputEditText = new EditText(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                GeneralFn.convertToDp(activity, 310),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15, 0, 15, 0);
        inputEditText.setLayoutParams(params);
        inputEditText.setBackgroundResource(R.drawable.ifl_edittext_background);
        inputEditText.setHint("Enter new value");
        inputEditText.setPadding(30, 30, 0, 24);
        inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        if (instafelDialog.getDialogThemeMode() == 2) {
            inputEditText.setTextColor(activity.getResources().getColor(R.color.ifl_black));
            inputEditText.setHighlightColor(activity.getResources().getColor(R.color.ifl_white));
            inputEditText.setHintTextColor(activity.getResources().getColor(R.color.ifl_sub_line_light));
            inputEditText.setLinkTextColor(activity.getResources().getColor(R.color.ifl_white));
        } else {
            inputEditText.setTextColor(activity.getResources().getColor(R.color.ifl_white));
            inputEditText.setHighlightColor(activity.getResources().getColor(R.color.ifl_black));
            inputEditText.setHintTextColor(activity.getResources().getColor(R.color.ifl_sub_line));
            inputEditText.setLinkTextColor(activity.getResources().getColor(R.color.ifl_black));
        }
        if (editTextMultiline) {
            inputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            inputEditText.setMaxLines(5);
            inputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                            (event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        // Yeni satıra geç
                        inputEditText.append("\n");
                        return true;
                    }
                    return false;
                }
            });
        } else {
            inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            inputEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        instafelDialog.addCustomView("edit_text", inputEditText);
        /*instafelDialog.addTextView(
                "dialog_desc",
                description,
                16,
                310,
                InstafelDialogTextType.DESCRIPTION,
                new InstafelDialogMargins(activity, 24, 24));*/
        instafelDialog.addSpace("button_top_space", 20);
        return new StringInputViews(inputEditText, instafelDialog);
    }

    public static void createSimpleAlertDialog(Activity act, String title, String message) {
        InstafelDialog instafelDialog = InstafelDialog.createSimpleDialog(act,
                title,
                message,
                "Okay",
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        act.finish();
                    }
                },
                null
        );
        instafelDialog.show();
    }

    public static void createSimpleAlertDialogNoFinish(Activity act, String title, String message) {
        InstafelDialog instafelDialog = InstafelDialog.createSimpleDialog(act,
                title,
                message,
                "Okay",
                null,
                null,
                null
        );
        instafelDialog.show();
    }

    public static InstafelDialog createSimpleDialog (Activity activity, String title, String description, String titlePozitive, String titleNegative, View.OnClickListener pozitiveListener, View.OnClickListener negativeListener) {
        InstafelDialog instafelDialog = new InstafelDialog(activity);
        instafelDialog.addSpace("top_space", 25);
        instafelDialog.addTextView(
                "dialog_title",
                title,
                30,
                0,
                InstafelDialogTextType.TITLE,
                new InstafelDialogMargins(activity, 0, 0));
        instafelDialog.addSpace("mid_space", 20);
        instafelDialog.addTextView(
                "dialog_desc",
                description,
                16,
                310,
                InstafelDialogTextType.DESCRIPTION,
                new InstafelDialogMargins(activity, 24, 24));
        instafelDialog.addSpace("button_top_space", 20);
        if (titlePozitive != null && titleNegative != null) {
            instafelDialog.addPozitiveAndNegativeButton("buttons", titlePozitive, titleNegative, pozitiveListener, negativeListener);
        } else if (titlePozitive != null) {
            instafelDialog.addPozitiveAndNegativeButton("buttons", titlePozitive, null, pozitiveListener, null);
        } else if (titleNegative != null) {
            instafelDialog.addPozitiveAndNegativeButton("buttons", null, titleNegative, null, negativeListener);
        }
        instafelDialog.addSpace("bottom_space", 27);
        return instafelDialog;
    }


    public void addPozitiveAndNegativeButton(String componentName, String titlePozitive, String titleNegative, View.OnClickListener pozitiveListener, View.OnClickListener negativeListener) {
        LinearLayout buttonLayout = new LinearLayout(act);
        buttonLayout.setId(View.generateViewId());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        if (titlePozitive != null) {
            LinearLayout pozitiveButton = generateButton(titlePozitive, 1);
            if (pozitiveListener == null) {
                pozitiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            } else {
                pozitiveButton.setOnClickListener(pozitiveListener);
            }
            buttonLayout.addView(pozitiveButton);
        }

        if (titlePozitive != null && titleNegative != null) {
            Space space = new Space(act);
            space.setLayoutParams(new ViewGroup.LayoutParams(
                    convertToDp(20),
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            buttonLayout.addView(space);
        }

        if (titleNegative != null) {
            LinearLayout negativeButton = generateButton(titleNegative, 0);
            if (negativeListener == null) {
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            } else {
                negativeButton.setOnClickListener(negativeListener);
            }
            buttonLayout.addView(negativeButton);
        }

        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        dialogItems.add(new InstafelDialogItem(componentName, buttonLayout));
    }

    private LinearLayout generateButton(String title, int type) {
        LinearLayout linearLayout = new LinearLayout(act);
        linearLayout.setId(View.generateViewId());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        RelativeLayout relativeLayout = new RelativeLayout(act);
        relativeLayout.setPadding(convertToDp(12), convertToDp(12), convertToDp(12), convertToDp(12));
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
        if (type == 0) {
            if (dialogThemeMode == 2) {
                relativeLayout.setBackground(act.getResources().getDrawable(R.drawable.ifl_button_secondary_background_light));
            } else {
                relativeLayout.setBackground(act.getResources().getDrawable(R.drawable.ifl_button_secondary_background_dark));
            }
        } else {
            if (dialogThemeMode == 2) {
                relativeLayout.setBackground(act.getResources().getDrawable(R.drawable.ifl_button_primary_background_light));
            } else {
                relativeLayout.setBackground(act.getResources().getDrawable(R.drawable.ifl_button_primary_background_dark));
            }
        }

        TextView textView = new TextView(act);
        textView.setText(title);
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textViewParams.setMarginStart(convertToDp(13));
        textViewParams.setMarginEnd(convertToDp(13));
        textViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        textView.setLayoutParams(textViewParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        if (type == 0) {
            if (dialogThemeMode == 2) {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_black));
            } else {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_white));
            }
        } else {
            if (dialogThemeMode == 2) {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_background_color_light));
            } else {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_background_color));
            }
        }

        relativeLayout.addView(textView);
        linearLayout.addView(relativeLayout);
        return linearLayout;
    }

    public int getDialogThemeMode() {
        return dialogThemeMode;
    }

    public View getView(String componentName) {
        View view = null;
        for (int i = 0; i < dialogItems.size(); i++) {
            if (dialogItems.get(i).getName() == componentName) {
                view = dialogItems.get(i).getView();
            }
        }
        return view;
    }

    public TextView getTextView(String componentName) {
        TextView view = null;
        for (int i = 0; i < dialogItems.size(); i++) {
            if (dialogItems.get(i).getName() == componentName) {
                view = (TextView) dialogItems.get(i).getView();
            }
        }
        return view;
    }

    public void addCustomView(String componentName, View view) {
        view.setId(View.generateViewId());
        dialogItems.add(new InstafelDialogItem(componentName, view));
    }

    public void addTextView(String componentName, String titleText, int textSize, int width, int instafelDialogTextType, InstafelDialogMargins dialogMargins) {
        TextView textView = new TextView(act);
        textView.setId(View.generateViewId());
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(titleText);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (componentName.equals("dialog_desc_left")) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

        if (width != 0) {
            textViewParams = new LinearLayout.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, act.getResources().getDisplayMetrics()),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        textViewParams.setMarginStart(dialogMargins.getStart());
        textViewParams.setMarginEnd(dialogMargins.getEnd());
        textView.setLayoutParams(textViewParams);

        if (instafelDialogTextType == InstafelDialogTextType.TITLE) {
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

            if (dialogThemeMode == 2) {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_black));
            } else {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_white));
            }
        } else if (instafelDialogTextType == InstafelDialogTextType.DESCRIPTION) {
            textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);

            if (dialogThemeMode == 2) {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_sub_line_light));
            } else {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_sub_line));
            }
        } else if (instafelDialogTextType == InstafelDialogTextType.SUBTEXT) {
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

            if (dialogThemeMode == 2) {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_sub_line_light));
            } else {
                textView.setTextColor(act.getResources().getColor(R.color.ifl_sub_line));
            }
        }

        dialogItems.add(new InstafelDialogItem(componentName, textView));
    }

    private void addComponentsIntoDialog() {
        for (int i = 0; i < dialogItems.size(); i++) {
            Log.v("Instafel", "Added " + dialogItems.get(i).getName() + " view into dialog.");
            dialogMainArea.addView(dialogItems.get(i).getView());
        }
        dialogRoot.addView(dialogMainArea);
        dialog.setContentView(dialogRoot);
    }

    private void setBaseLayout() {
        int themeMode = GeneralFn.getUiMode(act);

        dialogRoot = new CardView(act);
        dialogMainArea = new LinearLayout(act);


        // CardView'ı ayarla

        dialogRoot.setId(View.generateViewId());
        dialogRoot.setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, act.getResources().getDisplayMetrics()));
        CardView.LayoutParams dialogRoot_layoutParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        int dialogRootMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, act.getResources().getDisplayMetrics());
        // dialogRoot_layoutParams.setMargins(dialogRootMargin, dialogRootMargin, dialogRootMargin, dialogRootMargin);
        dialogRoot.setLayoutParams(dialogRoot_layoutParams);

        // LinaerLayout'u ayarla (main kısım bu)

        dialogMainArea.setId(View.generateViewId());
        dialogMainArea.setOrientation(LinearLayout.VERTICAL);
        dialogMainArea.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogMainArea.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        if (themeMode == 2) {
            dialogMainArea.setBackgroundColor(act.getResources().getColor(R.color.ifl_tile_color_light));
        } else {
            dialogMainArea.setBackgroundColor(act.getResources().getColor(R.color.ifl_tile_color));
        }

        dialogThemeMode = themeMode;

        Log.v("Instafel", "Content view'in teması " + themeMode + " olarak ayarlandi.");
    }

    public int convertToDp(int val) {
         return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, act.getResources().getDisplayMetrics());
    }
}