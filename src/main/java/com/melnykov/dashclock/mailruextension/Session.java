package com.melnykov.dashclock.mailruextension;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.melnykov.dashclock.mailruextension.util.Constants;

public class Session {

    private static Session instance;
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private long savedAt;

    private boolean isDirty;

    private Session(String accessToken, String refreshToken, int expiresIn, long savedAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.savedAt = savedAt;
    }

    public static synchronized Session getInstance() {
        if (instance == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MailRuExtensionApplication.getContext());
            instance = new Session(prefs.getString(Constants.KEY_ACCESS_TOKEN, null),
                    prefs.getString(Constants.KEY_REFRESH_TOKEN, null),
                    prefs.getInt(Constants.KEY_EXPIRES_IN, 0),
                    prefs.getLong(Constants.KEY_SAVED_AT, 0));
        }

        return instance;
    }

    public boolean isValid() {
        return isAuthorized() && !sessionExpired();
    }

    public boolean isAuthorized() {
        return accessToken != null;
    }

    private boolean sessionExpired() {
        long now = System.currentTimeMillis() / 1000;
        return now > savedAt + expiresIn;
    }

    public Session setAccessToken(String accessToken, int expiresIn) {
        if (accessToken != this.accessToken) {
            this.accessToken = accessToken;
            this.expiresIn = expiresIn;
            this.isDirty = true;
        }
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

    public synchronized void save() {
        if (isDirty) {
            savedAt = System.currentTimeMillis() / 1000;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MailRuExtensionApplication.getContext());
            prefs.edit().putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
                    .putInt(Constants.KEY_EXPIRES_IN, expiresIn)
                    .putLong(Constants.KEY_SAVED_AT, savedAt)
                    .putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                    .apply();
            isDirty = false;
        }
    }

    public synchronized void destroy() {
        this.accessToken = null;
        this.refreshToken = null;
        this.expiresIn = 0;
        this.savedAt = 0;
        instance = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MailRuExtensionApplication.getContext());
        prefs.edit().remove(Constants.KEY_ACCESS_TOKEN)
                .remove(Constants.KEY_REFRESH_TOKEN)
                .remove(Constants.KEY_EXPIRES_IN)
                .remove(Constants.KEY_SAVED_AT)
                .apply();
    }
}
