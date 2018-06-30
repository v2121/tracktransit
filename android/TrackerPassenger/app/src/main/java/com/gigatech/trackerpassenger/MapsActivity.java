package com.gigatech.trackerpassenger;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gigatech.trackerpassenger.models.Vehicle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int ROUTE_SELECT = 1;

    private TextView mRoute;
    private Dialog mDialog;

    private GoogleMap mMap;
    private ConcurrentHashMap<Integer, Marker> vehicleMap;
    private Handler msgHandler;

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

        msgHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == TrackService.UPDATE_VEHICLES_ON_MAP) {
                    renderVehicles(TrackService.getInstance().getVehicles());
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                }
            }
        };

        vehicleMap = new ConcurrentHashMap<>();
        mRoute = (TextView) findViewById(R.id.txt_search);

        mRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, RouteActivity.class);
                startActivityForResult(i, ROUTE_SELECT);
            }
        });
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
        TrackService.getInstance().start();
        TrackService.getInstance().setHandler(msgHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TrackService.getInstance().setHandler(null);
        TrackService.getInstance().stop();
    }

    private void zoomToMyLocation() {
        if (mMap != null) {
            LocationManager locationManager = (LocationManager) MainApplication.getContext().getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
            }
        }
    }

    public void drawVehicle(Vehicle v) {
        Log.d(TAG, "Drawing vehicle: " + v.vehicle_id);

        if (mMap == null) return;
        LatLng l = new LatLng(v.latitude, v.longitude);

        Marker vehicle_marker = vehicleMap.get(v.vehicle_id);
        if (vehicle_marker == null) {
            vehicle_marker = mMap.addMarker(new MarkerOptions().position(l)
                .title(v.route_number)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus))
            );
        } else {
            vehicle_marker.setPosition(l);
        }
        vehicleMap.put(v.vehicle_id, vehicle_marker);
    }

    public void removeVehicleById(Integer vehicle_id) {
        if (mMap == null) return;
        Marker vehicle_marker = vehicleMap.get(vehicle_id);
        if (vehicle_marker != null) {
            vehicle_marker.remove();
        }
        vehicleMap.remove(vehicle_id);
    }

    public void renderVehicles(Vehicle[] vehicles) {
        Log.d(TAG, "Total vehicles available: " + vehicles.length);

        HashMap<Integer,Vehicle> vs = new HashMap<>();
        for (Vehicle v : vehicles) {
            vs.put(v.vehicle_id, v);
            drawVehicle(v);
        }

        for (Integer v_id : vehicleMap.keySet()) {
            Vehicle v = vs.get(v_id);
            if (v == null) {
                removeVehicleById(v_id);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Account.getInstance().setUser(null);
                Account.getInstance().save();

                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ROUTE_SELECT) {
            if (resultCode == RESULT_OK) {
                String route_number = data.getStringExtra(RouteActivity.ROUTE_DATA);
                if (route_number.equals("")) {
                    route_number = null;
                }
                TrackService.getInstance().setRoute(route_number);
                if (route_number == null) {
                    mRoute.setText(getString(R.string.near_me));
                } else {
                    mRoute.setText(getString(R.string.with_number) + " " + route_number);
                }
                mDialog = MainApplication.getDialog(MapsActivity.this, getString(R.string.please_wait));
                mDialog.show();
            }
        }
    }
}
