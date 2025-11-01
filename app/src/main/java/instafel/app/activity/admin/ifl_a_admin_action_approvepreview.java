package instafel.app.activity.admin;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import instafel.app.R;
import instafel.app.api.models.InstafelResponse;
import instafel.app.api.requests.ApiCallbackInterface;
import instafel.app.api.requests.ApiPostAdmin;
import instafel.app.managers.PreferenceManager;
import instafel.app.ota.IflEnvironment;
import instafel.app.ui.TileLarge;
import instafel.app.ui.TileLargeEditText;
import instafel.app.utils.GeneralFn;
import instafel.app.utils.types.PreferenceKeys;
import instafel.app.utils.dialog.InstafelDialog;
import instafel.app.utils.dialog.InstafelDialogMargins;
import instafel.app.utils.dialog.InstafelDialogTextType;

public class ifl_a_admin_action_approvepreview extends AppCompatActivity implements ApiCallbackInterface {

    PreferenceManager preferenceManager;
    TextView buttonText;
    LinearLayout button;
    TileLargeEditText tileChangelog;
    TileLarge tileGenerationId;
    boolean clickLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_admin_action_approvepreview);

        preferenceManager = new PreferenceManager(this);
        button = findViewById(R.id.ifl_button_approvepreview);
        buttonText = findViewById(R.id.ifl_text_button);
        tileChangelog = findViewById(R.id.ifl_tile_setchangelog);
        tileGenerationId = findViewById(R.id.ifl_tile_selectbackupfile);

        EditText editText = tileChangelog.getEditTextView();
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMaxLines(20);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    editText.append("\n");
                    return true;
                }
                return false;
            }
        });

        String GENERATION_ID = IflEnvironment.getGenerationId(this);
        tileGenerationId.setSubtitleText(GENERATION_ID);
        buttonText.setText("Approve this preview");

        button.setOnClickListener(view -> {
            try {
               if (!clickLock) {
                   ApiPostAdmin apiPostAdmin = new ApiPostAdmin(
                           ifl_a_admin_action_approvepreview.this,
                           19,
                           preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_username, "null"),
                           preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_password, "null"),
                           new JSONObject().put("gen_id", GENERATION_ID).put("clog", editText.getText().toString()));
                   apiPostAdmin.execute(GeneralFn.getApiUrl(ifl_a_admin_action_approvepreview.this) + "/user_admin/approve-preview");
                   clickLock = true;
               } else {
                   Toast.makeText(ifl_a_admin_action_approvepreview.this, "Please wait for finish process", Toast.LENGTH_SHORT).show();
               }

            } catch (JSONException e) {
                Toast.makeText(ifl_a_admin_action_approvepreview.this, "Error while building request", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            }});
    }

    public void showDialog(String title, String description) {
        InstafelDialog instafelDialog = new InstafelDialog(this);
        instafelDialog.addSpace("top_space", 25);
        instafelDialog.addTextView(
                "dialog_title",
                title,
                30,
                0,
                InstafelDialogTextType.TITLE,
                new InstafelDialogMargins(this, 0, 0));
        instafelDialog.addSpace("mid_space", 20);
        instafelDialog.addTextView(
                "dialog_desc",
                description,
                16,
                310,
                InstafelDialogTextType.DESCRIPTION,
                new InstafelDialogMargins(this, 24, 24));
        instafelDialog.addSpace("button_top_space", 20);
        instafelDialog.addPozitiveAndNegativeButton(
                "buttons",
                "Okay",
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        instafelDialog.dismiss();
                        finish();
                    }
                },
                null);
        instafelDialog.addSpace("bottom_space", 27);
        instafelDialog.show();
    }

    @Override
    public void getResponse(InstafelResponse instafelResponse, int taskId) {
        if (taskId == 19) {
            if (instafelResponse != null) {
                try {
                    if (instafelResponse.getStatus().equals("SUCCESS")) {
                        showDialog("Success", instafelResponse.getDesc());
                    } else if (instafelResponse.getStatus().equals("ALREADY_APPROVED")) {
                        showDialog("Already Approved", instafelResponse.getDesc());
                    } else {
                        showDialog(instafelResponse.getStatus(), instafelResponse.getDesc());
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error while parsing response.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void getResponse(String rawResponse, int taskId) {

    }
}