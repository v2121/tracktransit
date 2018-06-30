package com.gigatech.trackerpassenger.models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;
}
