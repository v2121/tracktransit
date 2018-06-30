package com.gigatech.trackerpassenger;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gigatech.trackerpassenger.models.LoginRequest;
import com.gigatech.trackerpassenger.models.LoginResponse;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btnLogin;
    private EditText mUsername;
    private EditText mPassword;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.login);
        mUsername = (EditText) findViewById(R.id.input_username);
        mPassword = (EditText) findViewById(R.id.input_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setEnabled(false);
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if (username.equals("") || password.equals("")) {
                    btnLogin.setEnabled(true);
                    mDialog = MainApplication.getDialogWithPositiveButton(LoginActivity.this, getString(R.string.invalid_username_or_password), null);
                    mDialog.show();
                } else {
                    mDialog = MainApplication.getDialog(LoginActivity.this, getString(R.string.please_wait));
                    mDialog.show();
                    doLogin(username, password);
                }
            }
        });
    }

    private void doLogin(String username, String password) {
        LoginRequest r = new LoginRequest();
        r.username = username;
        r.password = password;

        RetrofitService.EasyCallback<LoginResponse> loginCallback = new RetrofitService.EasyCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                if (!response.user.IsValid()) {
                    showLoginError();
                    return;
                }

                Account.getInstance().setUser(response.user);
                Account.getInstance().save();

                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);

                if (mDialog != null) mDialog.dismiss();
                finish();
            }

            @Override
            public void onError(int responseCode, String responseString, Throwable t) {
                showLoginError();
            }

            private void showLoginError() {
                btnLogin.setEnabled(true);
                if (mDialog != null) mDialog.dismiss();
                mDialog = MainApplication.getDialogWithPositiveButton(LoginActivity.this, getString(R.string.invalid_username_or_password), null);
                mDialog.show();
            }
        };

        Call<LoginResponse> call = ApiService.get().userLogin(r);
        call.enqueue(loginCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPassword.setText("");
    }
}
