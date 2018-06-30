package com.gigatech.trackerpassenger;

import com.gigatech.trackerpassenger.models.LocationRequest;
import com.gigatech.trackerpassenger.models.LocationResponse;
import com.gigatech.trackerpassenger.models.LoginRequest;
import com.gigatech.trackerpassenger.models.LoginResponse;
import com.gigatech.trackerpassenger.models.RouteResponse;
import com.gigatech.trackerpassenger.models.TokenRequest;
import com.gigatech.trackerpassenger.models.TokenResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
        @POST("/user/login")
        Call<LoginResponse> userLogin(@Body LoginRequest request);

        @POST("/user/validate_token")
        Call<TokenResponse> checkToken(@Body TokenRequest request);

        @POST("/vehicles")
        Call<LocationResponse> getVehicleLocations(@Body LocationRequest request);

        @GET("/routes")
        Call<RouteResponse> getRoutes();
    }
}
