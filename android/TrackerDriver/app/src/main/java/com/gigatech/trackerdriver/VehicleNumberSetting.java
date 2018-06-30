package com.gigatech.trackerdriver;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class VehicleNumberSetting extends AppCompatActivity {

    private EditText mVehicleNumber;
    private Button mCancel;
    private Button mDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_vehicle_number_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.action_bar, null);
            actionBar.setCustomView(v);
        }

        mVehicleNumber = (EditText) findViewById(R.id.input_vehicle_number);
        mCancel = (Button) findViewById(R.id.cancel);
        mDone = (Button) findViewById(R.id.done);

        mVehicleNumber.setText(Account.getInstance().getVehicleNumber());

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCancel.setEnabled(false);
                mDone.setEnabled(false);
                onBackPressed();
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCancel.setEnabled(false);
                mDone.setEnabled(false);

                String vehicle_number = mVehicleNumber.getText().toString();
                if (vehicle_number.equals("")) {
                    Dialog dialog = MainApplication.getDialogWithPositiveButton(VehicleNumberSetting.this, getString(R.string.invalid_vehicle_number), null);
                    dialog.show();
                    mCancel.setEnabled(true);
                    mDone.setEnabled(true);
                } else {
                    Account.getInstance().setVehicleNumber(vehicle_number.toUpperCase());
                    Account.getInstance().save();
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }
}
