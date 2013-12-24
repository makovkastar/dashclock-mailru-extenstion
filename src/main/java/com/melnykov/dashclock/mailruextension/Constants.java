package com.melnykov.dashclock.mailruextension;

public class Constants {
    public static final String APP_ID = "705871";
    public static final String APP_PRIVATE_KEY = "04ccd4c7718e0de1e2cf32120139cf2f";
    public static final String APP_SECRET_KEY = "c8c192cba00e308d2ebf45d7712365cd";

    public static final String AUTH_URL = "https://connect.mail.ru/oauth/authorize";
    public static final String REDIRECT_URL = "http://connect.mail.ru/oauth/success";

    /**
     * Shared preferences keys
     */
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_SAVED_AT = "saved_at";

    public static final boolean DEBUG = true;
}
