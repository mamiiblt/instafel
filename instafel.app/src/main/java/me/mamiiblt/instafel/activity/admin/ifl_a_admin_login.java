package me.mamiiblt.instafel.activity.admin;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.api.requests.admin.AdminLogin;
import me.mamiiblt.instafel.ui.LoadingBar;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;

public class ifl_a_admin_login extends AppCompatActivity {

    EditText tileUsername, tilePassword;
    LinearLayout tileLogin;
    InstafelDialog waitingApiDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_admin_login);

        tileUsername = findViewById(R.id.ifl_tile_admin_username);
        tilePassword = findViewById(R.id.ifl_tile_admin_password);
        tileLogin = findViewById(R.id.ifl_tile_admin_login);
        waitingApiDialog = new InstafelDialog(this);
        waitingApiDialog = new InstafelDialog(this);
        waitingApiDialog.addSpace("top_space", 25);
        LoadingBar loadingBar = new LoadingBar(this);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
        int i = (int) ((25 * this.getResources().getDisplayMetrics().density) + 0.5f);
        marginLayoutParams.setMargins(i, 0, i, 0);
        loadingBar.setLayoutParams(marginLayoutParams);
        waitingApiDialog.addCustomView("loading_bar", loadingBar);
        waitingApiDialog.addSpace("button_top_space", 25);

        tileLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitingApiDialog.show();
                String username = tileUsername.getText().toString();
                String password = tilePassword.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    new AdminLogin(ifl_a_admin_login.this, username, password, waitingApiDialog)
                            .execute(GeneralFn.getApiUrl(ifl_a_admin_login.this) + "/admin/user/login");
                } else {
                    waitingApiDialog.hide();
                    InstafelDialog.createSimpleAlertDialog(ifl_a_admin_login.this, "Error", ifl_a_admin_login.this.getString(R.string.ifl_a12_05));
                }
            }
        });

    }


}