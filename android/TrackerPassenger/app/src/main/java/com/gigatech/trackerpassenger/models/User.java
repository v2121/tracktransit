package com.gigatech.trackerpassenger.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("username")
    public String username;

    @SerializedName("first_name")
    public String first_name;

    @SerializedName("last_name")
    public String last_name;

    @SerializedName("token")
    public String token;

    public boolean IsValid() {
        if (username.equals("") || token.equals("")) {
            return false;
        }
        return true;
    }
}
