package com.melnykov.dashclock.mailruextension;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class Session {

    private static Session instance;
    private String accessToken;
    private String refreshToken;
    private int expiresIn;

    private Session(String accessToken, String refreshToken, int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public static synchronized Session getInstance(Context ctx) {
        if (instance == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            instance = new Session(prefs.getString(Constants.KEY_ACCESS_TOKEN, null),
                    prefs.getString(Constants.KEY_REFRESH_TOKEN, null),
                    prefs.getInt(Constants.KEY_EXPIRES_IN, 0));
        }

        return instance;
    }

    public boolean isValid() {
        return accessToken != null;
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

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Session setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void save(Context ctx) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit()
                .putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                .putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
                .putInt(Constants.KEY_EXPIRES_IN, expiresIn)
                .apply();
    }
}
