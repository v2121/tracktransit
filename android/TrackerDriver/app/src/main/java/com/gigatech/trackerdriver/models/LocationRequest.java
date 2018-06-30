package com.gigatech.trackerdriver.models;

import com.google.gson.annotations.SerializedName;

public class LocationRequest {
    @SerializedName("vehicle_id")
    public int vehicle_id;

    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;
}
