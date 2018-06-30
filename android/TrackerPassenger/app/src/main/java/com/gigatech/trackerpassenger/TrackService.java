package com.gigatech.trackerpassenger;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.gigatech.trackerpassenger.models.LocationRequest;
import com.gigatech.trackerpassenger.models.LocationResponse;
import com.gigatech.trackerpassenger.models.Vehicle;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import retrofit2.Call;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TrackService {

    private static final String TAG = "TrackService";
    public static final int UPDATE_VEHICLES_ON_MAP = 1;

    private static TrackService instance;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> reportFetcherFuture;
    private Runnable reportFetcher;
    private String mRoute;

    private Vehicle[] vehicles;

    private WeakReference<Handler> handler;

    public static synchronized TrackService getInstance() {
        if (instance == null) {
            instance = new TrackService();
        }
        return instance;
    }

    public static void destroy() {
        if (instance != null) {
            instance.stop();
            instance.scheduler.shutdownNow();
            instance = null;
        }
    }

    private TrackService() {
        handler = new WeakReference<>(null);
        scheduler = Executors.newScheduledThreadPool(1);
        reportFetcher = new Runnable() {
            @Override
            public void run() {
                LocationManager locationManager = (LocationManager) MainApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);

                if (location == null) return;

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LocationRequest location_request = new LocationRequest();
                location_request.latitude = latitude;
                location_request.longitude = longitude;
                location_request.route = getRoute();

                Call<LocationResponse> vehicles_call = ApiService.get().getVehicleLocations(location_request);
                try {
                    LocationResponse resp = vehicles_call.execute().body();
                    setVehicles(resp.vehicles);
                    Handler h = handler.get();
                    if (h != null) {
                        h.sendEmptyMessage(UPDATE_VEHICLES_ON_MAP);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error in vehicles request: ", e);
                }
            }
        };
    }

    private synchronized String getRoute() {
        return mRoute;
    }

    public synchronized void setRoute(String route) {
        mRoute = route;
    }

    private synchronized void setVehicles(Vehicle[] l_vehicles) {
        vehicles = l_vehicles;
    }

    public synchronized Vehicle[] getVehicles() {
        return vehicles;
    }

    public void setHandler(Handler h) {
        handler = new WeakReference<Handler>(h);
    }

    public void start() {
        reportFetcherFuture = scheduler.scheduleWithFixedDelay(reportFetcher, 5, 5, SECONDS);
    }

    public void stop() {
        if (reportFetcherFuture != null) {
            reportFetcherFuture.cancel(true);
        }
    }
}
