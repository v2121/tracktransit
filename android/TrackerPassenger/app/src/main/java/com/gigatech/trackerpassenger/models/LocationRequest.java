package com.gigatech.trackerpassenger.models;

import com.google.gson.annotations.SerializedName;

public class LocationRequest {
    @SerializedName("lat")
    public double latitude;

    @SerializedName("lng")
    public double longitude;

    @SerializedName("route")
    public String route;
}
