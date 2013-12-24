package com.melnykov.dashclock.mailruextension;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class Session {

    private static Session instance;
    private String accessToken;
    private String refreshToken;
    private int expiresIn;

    private long savedAt;

    private Session(String accessToken, String refreshToken, int expiresIn, long savedAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.savedAt = savedAt;
    }

    public static synchronized Session getInstance(Context ctx) {
        if (instance == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            instance = new Session(prefs.getString(Constants.KEY_ACCESS_TOKEN, null),
                    prefs.getString(Constants.KEY_REFRESH_TOKEN, null),
                    prefs.getInt(Constants.KEY_EXPIRES_IN, 0),
                    prefs.getLong(Constants.KEY_SAVED_AT, 0));
        }

        return instance;
    }

    public boolean isValid() {
        long now = System.currentTimeMillis() / 1000;
        return accessToken != null && (now < savedAt + expiresIn);
    }

    public Session setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Session setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public Session setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public synchronized void save(Context ctx) {
        savedAt = Calendar.getInstance().getTimeInMillis() / 1000;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                .putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
                .putInt(Constants.KEY_EXPIRES_IN, expiresIn)
                .putLong(Constants.KEY_SAVED_AT, savedAt)
                .apply();
    }

    public synchronized void destroy(Context ctx) {
        this.accessToken = null;
        this.refreshToken = null;
        this.expiresIn = 0;
        instance = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().remove(Constants.KEY_ACCESS_TOKEN)
                .remove(Constants.KEY_REFRESH_TOKEN)
                .remove(Constants.KEY_EXPIRES_IN)
                .apply();
    }
}
