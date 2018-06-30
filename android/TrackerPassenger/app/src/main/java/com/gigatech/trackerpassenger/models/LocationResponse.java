package com.gigatech.trackerpassenger.models;

import com.google.gson.annotations.SerializedName;

public class LocationResponse {
    @SerializedName("vehicles")
    public Vehicle[] vehicles;
}
