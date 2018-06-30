package com.gigatech.trackerdriver;

import android.content.Context;
import android.content.SharedPreferences;

public class Account {

    private static final String SERVER_BASE_URL = "http://ec2-52-89-216-21.us-west-2.compute.amazonaws.com";
    private static final String APP_ADMIN_PIN = "1248";
    private static final String APP_PREFS = "track_transit";
    private static final String PREF_VEHICLE_NUMBER = "vehicle_number";
    private static final String PREF_ROUTE_NUMBER = "route_number";
    private static final String PREF_VEHICLE_ID = "vehicle_id";

    private static Account instance;

    private String vehicleNumber;
    private int vehicleId;
    private String routeNumber;

    public static synchronized Account getInstance() {
        if (instance == null) {
            instance = new Account();
        }
        return instance;
    }

    public static void destroy() {
        if (instance != null) {
            instance = null;
        }
    }

    private Account() {
        load();
    }

    public String getServerName() {
        return SERVER_BASE_URL;
    }

    public String getAdminPin() {
        return APP_ADMIN_PIN;
    }

    public void setVehicleNumber(String vehicle_number) {
        vehicleNumber = vehicle_number;
    }

    public String getVehicleNumber() {
        if (vehicleNumber == null) return "";
        return vehicleNumber;
    }

    public void setRouteNumber(String route) {
        routeNumber = route;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setVehicleId(int id) {
        vehicleId = id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void save() {
        SharedPreferences prefs = MainApplication.getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();

        e.putString(PREF_VEHICLE_NUMBER, vehicleNumber);
        e.putString(PREF_ROUTE_NUMBER, routeNumber);
        e.putInt(PREF_VEHICLE_ID, vehicleId);

        e.commit();
    }

    private void load() {
        SharedPreferences prefs = MainApplication.getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        vehicleNumber = prefs.getString(PREF_VEHICLE_NUMBER, "");
        vehicleId = prefs.getInt(PREF_VEHICLE_ID, 0);
        routeNumber = prefs.getString(PREF_ROUTE_NUMBER, "");
    }
}
