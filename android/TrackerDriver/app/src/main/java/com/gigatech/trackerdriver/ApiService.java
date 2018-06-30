package com.gigatech.trackerdriver;

import com.gigatech.trackerdriver.models.LoginRequest;
import com.gigatech.trackerdriver.models.LoginResponse;
import com.gigatech.trackerdriver.models.LocationRequest;
import com.gigatech.trackerdriver.models.LocationResponse;
import com.gigatech.trackerdriver.models.LogoutRequest;
import com.gigatech.trackerdriver.models.LogoutResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class ApiService {

    private static RetrofitService<Apis> rinstance;
    private static Apis service;

    private static synchronized RetrofitService<Apis> getInstance() {
        if (rinstance == null) {
            rinstance = new RetrofitService<Apis>(Apis.class, Account.getInstance().getServerName());
        }
        return rinstance;
    }

    public static synchronized Apis get() {
        if (service == null) {
            service = getInstance().service();
        }
        return service;
    }

    public static synchronized OkHttpClient getClient() {
    return getInstance().client();
    }

    public static synchronized void destroy() {
        if (service != null) {
            service = null;
        }
        if (rinstance != null) {
            rinstance = null;
        }
    }

    public interface Apis {
        @POST("/vehicle/login")
        Call<LoginResponse> loginVehicle(@Body LoginRequest request);

        @PUT("/vehicle/location")
        Call<LocationResponse> updateVehicleLocation(@Body LocationRequest request);

        @POST("/vehicle/logout")
        Call<LogoutResponse> logoutVehicle(@Body LogoutRequest request);
    }
}
