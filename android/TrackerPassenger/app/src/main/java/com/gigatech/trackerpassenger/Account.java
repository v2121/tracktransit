package com.gigatech.trackerpassenger;

import android.content.Context;
import android.content.SharedPreferences;

import com.gigatech.trackerpassenger.models.User;

public class Account {

    private static final String SERVER_BASE_URL = "http://ec2-52-89-216-21.us-west-2.compute.amazonaws.com";
    private static final String APP_PREFS = "track_transit_passenger";

    private static Account instance;
    private User mUser;

    public static synchronized Account getInstance() {
        if (instance == null) {
            instance = new Account();
        }
        return instance;
    }

    public static void destroy() {
        if (instance != null) {
            instance = null;
        }
    }

    private Account() {
        load();
    }

    public String getServerName() {
        return SERVER_BASE_URL;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getToken() {
        if (mUser != null) {
            return mUser.token;
        }
        return null;
    }

    public void save() {
        SharedPreferences prefs = MainApplication.getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        if (mUser != null) {
            String user_data = MainApplication.getGson().toJson(mUser);
            e.putString("user", user_data);
        } else {
            e.putString("user", "");
        }
        e.commit();
    }

    private void load() {
        SharedPreferences prefs = MainApplication.getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);

        String user_data = prefs.getString("user", "");
        if (!user_data.equals("")) {
            mUser = MainApplication.getGson().fromJson(user_data, User.class);
        } else {
            mUser = null;
        }
    }

    public void logout() {
        Account.getInstance().setUser(null);
        Account.getInstance().save();
    }
}
