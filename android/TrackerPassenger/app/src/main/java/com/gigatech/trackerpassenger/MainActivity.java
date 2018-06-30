package com.gigatech.trackerpassenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gigatech.trackerpassenger.models.TokenRequest;
import com.gigatech.trackerpassenger.models.TokenResponse;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private static final int LOADING_DELAY = 1000;
    private View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLogin();
            }
        }, LOADING_DELAY);
    }

    private void checkLogin() {
        if (Account.getInstance().getUser() != null) {
            RetrofitService.EasyCallback<TokenResponse> tokenCheckCallback = new RetrofitService.EasyCallback<TokenResponse>() {
                @Override
                public void onSuccess(TokenResponse response) {
                    if (response.valid) {
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showLogin();
                    }
                }

                @Override
                public void onError(int responseCode, String responseString, Throwable t) {
                    showLogin();
                }
            };

            TokenRequest r = new TokenRequest();
            r.token = Account.getInstance().getToken();
            Call<TokenResponse> call = ApiService.get().checkToken(r);
            call.enqueue(tokenCheckCallback);
        } else {
            showLogin();
        }
    }

    private void showLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
