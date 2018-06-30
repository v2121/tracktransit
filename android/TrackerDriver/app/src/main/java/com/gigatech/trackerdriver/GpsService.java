package com.gigatech.trackerdriver;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class GpsService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "GpsService";
    public static final int FOCUS_ZOOM = 16;

    private static GpsService instance;
    private GoogleApiClient mGoogleApiClient;
    private List<LocationListener> mObservers;
    private double mCurrentLatitude;
    private double mCurrentLongitude;

    public static synchronized GpsService getInstance() {
        if (instance == null) {
            instance = new GpsService();
        }
        return instance;
    }

    private GpsService() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MainApplication.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }
        mObservers = new ArrayList<>();
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    public void stop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    public static void destroy() {
        if (instance != null) {
            instance.stop();
            instance = null;
        }
    }

    public double getLatitude() {
        return mCurrentLatitude;
    }

    public double getLongitude() {
        return mCurrentLongitude;
    }

    public void add(LocationListener l) {
        mObservers.add(l);
    }

    public void remove(LocationListener l) {
        mObservers.remove(l);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(8000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location: (" + location.getLatitude() + "," + location.getLongitude() + ")");

        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();

        for (LocationListener l : mObservers) {
            l.onLocationChanged(location.getLatitude(), location.getLongitude());
        }
    }

    public interface LocationListener {
        void onLocationChanged(double latitude, double longitude);
    }
}
