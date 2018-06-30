package com.gigatech.trackerpassenger;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitService<T> {

    private static final String TAG = "RetrofitService";
    private OkHttpClient httpClient;
    private Retrofit retrofit;
    private T service;

    public RetrofitService(Class<T> serviceClass, String baseUrl) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override public void log(String message) {
                Log.d(TAG, message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
        builder.build();

        httpClient = builder.build();
        retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(MainApplication.getGson()))
                .callFactory(httpClient).build();
        service = retrofit.create(serviceClass);
    }

    public abstract static class EasyCallback<T> implements Callback<T> {

        @Override
        public void onResponse(Call<T> call, retrofit2.Response<T> response) {
            if (response.isSuccessful()) {
                onSuccess(response.body());
            } else {
                String v = "";
                try {
                    v = response.errorBody().string();
                } catch (IOException e) {
                }
                onError(response.code(), v, null);
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            onError(-1, null, t);
        }

        public abstract void onSuccess(T response);

        public abstract void onError(int responseCode, String responseString, Throwable t);
    }

    public T service() {
        return service;
    }

    public OkHttpClient client() {
        return httpClient;
    }
}
