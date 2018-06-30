package com.gigatech.trackerdriver.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("vehicle_id")
    public int vehicle_id;

    @SerializedName("route_number")
    public String route_number;
}
