package com.gigatech.trackerdriver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gigatech.trackerdriver.models.LogoutRequest;
import com.gigatech.trackerdriver.models.LogoutResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private TextView txtRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.action_bar, null);
            actionBar.setCustomView(v);
        }

        findViewById(R.id.end_trip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog d = MainApplication.getDialogWithYesNoButtons(MapsActivity.this, getString(R.string.end_trip_confirmation), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doEndTrip();
                    }
                }, null);
                d.show();
            }
        });

        txtRoute = (TextView) findViewById(R.id.route_number);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        zoomToMyLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TripController.getInstance().start();
        txtRoute.setText("BUS: " + Account.getInstance().getRouteNumber());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TripController.getInstance().stop();
    }

    @Override
    public void onBackPressed() {

    }

    private void doEndTrip() {
        TripController.getInstance().stop();
        Account.getInstance().setRouteNumber("");
        Account.getInstance().setVehicleId(0);
        Account.getInstance().save();

        RetrofitService.EasyCallback<LogoutResponse> logoutCallback = new RetrofitService.EasyCallback<LogoutResponse>() {
            @Override
            public void onSuccess(LogoutResponse response) {
                Log.v(TAG, "Logout response success");
            }

            @Override
            public void onError(int responseCode, String responseString, Throwable t) {
                Log.v(TAG, "Logout response error");
            }
        };

        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.vehicle_number = Account.getInstance().getVehicleNumber();
        Call<LogoutResponse> logoutCall = ApiService.get().logoutVehicle(logoutRequest);
        logoutCall.enqueue(logoutCallback);

        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    private void zoomToMyLocation() {
        if (mMap != null) {
            double latitude = GpsService.getInstance().getLatitude();
            double longitude = GpsService.getInstance().getLongitude();

            LatLng currentLocation = null;
            if (latitude != 0 && longitude != 0) {
                currentLocation = new LatLng(latitude, longitude);
            } else {
                LocationManager locationManager = (LocationManager) MainApplication.getContext().getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }

            if (currentLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, GpsService.FOCUS_ZOOM));
            }
        }
    }
}
