package com.gigatech.trackerdriver;

import android.util.Log;

import com.gigatech.trackerdriver.models.LocationRequest;
import com.gigatech.trackerdriver.models.LocationResponse;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import retrofit2.Call;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TripController {

    private static final String TAG = "TripController";

    private static TripController instance;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> reporterFuture;
    private Runnable reporter;

    public static synchronized TripController getInstance() {
        if (instance == null) {
            instance = new TripController();
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

    private TripController() {
        scheduler = Executors.newScheduledThreadPool(1);
        reporter = new Runnable() {
            @Override
            public void run() {
                GpsService gps = GpsService.getInstance();
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                int vehicle_id = Account.getInstance().getVehicleId();

                LocationRequest location_request = new LocationRequest();
                location_request.latitude = latitude;
                location_request.longitude = longitude;
                location_request.vehicle_id = vehicle_id;

                Call<LocationResponse> loc_call = ApiService.get().updateVehicleLocation(location_request);
                try {
                    loc_call.execute().body();
                } catch (IOException e) {
                    Log.e(TAG, "Error in location request: ", e);
                }
            }
        };
    }

    public void start() {
        reporterFuture = scheduler.scheduleWithFixedDelay(reporter, 5, 5, SECONDS);
    }

    public void stop() {
        if (reporterFuture != null) {
            reporterFuture.cancel(true);
        }
    }
}
