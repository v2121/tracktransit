package com.gigatech.trackerdriver.models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("vehicle_number")
    public String vehicle_number;

    @SerializedName("pin")
    public String pin;
}
