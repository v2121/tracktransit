package com.gigatech.trackerdriver;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gigatech.trackerdriver.models.LoginRequest;
import com.gigatech.trackerdriver.models.LoginResponse;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText pinInput;
    private Button btnLogin;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_main);

        pinInput = (EditText) findViewById(R.id.input_pin);
        btnLogin = (Button) findViewById(R.id.login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setEnabled(false);

                String pin = pinInput.getText().toString();
                if (pin.equals("")) {
                    btnLogin.setEnabled(true);
                    mDialog = MainApplication.getDialogWithPositiveButton(MainActivity.this, getString(R.string.invalid_pin), null);
                    mDialog.show();
                } else if (pin.equals(Account.getInstance().getAdminPin())) {
                    // Open admin settings
                    Intent intent = new Intent(MainActivity.this, VehicleNumberSetting.class);
                    startActivity(intent);

                    btnLogin.setEnabled(true);
                } else {
                    mDialog = MainApplication.getDialog(MainActivity.this, getString(R.string.please_wait));
                    mDialog.show();
                    doLogin(pin);
                }
            }
        });
    }

    private void doLogin(String pin) {
        LoginRequest r = new LoginRequest();
        r.vehicle_number = Account.getInstance().getVehicleNumber();
        r.pin = pin;

        RetrofitService.EasyCallback<LoginResponse> loginCallback = new RetrofitService.EasyCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                Account.getInstance().setVehicleId(response.vehicle_id);
                Account.getInstance().setRouteNumber(response.route_number);
                Account.getInstance().save();

                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);

                if (mDialog != null) mDialog.dismiss();
                finish();
            }

            @Override
            public void onError(int responseCode, String responseString, Throwable t) {
                btnLogin.setEnabled(true);
                if (mDialog != null) mDialog.dismiss();
                mDialog = MainApplication.getDialogWithPositiveButton(MainActivity.this, getString(R.string.invalid_pin), null);
                mDialog.show();
            }
        };

        Call<LoginResponse> call = ApiService.get().loginVehicle(r);
        call.enqueue(loginCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pinInput.setText("");
    }
}
