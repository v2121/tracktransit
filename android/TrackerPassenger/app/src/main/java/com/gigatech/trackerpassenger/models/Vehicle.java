package com.gigatech.trackerpassenger.models;

import com.google.gson.annotations.SerializedName;

public class Vehicle {
    @SerializedName("vehicle_id")
    public Integer vehicle_id;

    @SerializedName("route_number")
    public String route_number;

    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;
}
