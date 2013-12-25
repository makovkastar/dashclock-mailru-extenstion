package com.melnykov.dashclock.mailruextension.util;

public class Constants {
    public static final String APP_ID = "705871";
    public static final String APP_SECRET_KEY = "c8c192cba00e308d2ebf45d7712365cd";

    public static final String AUTH_URL = "https://connect.mail.ru/oauth/authorize";
    public static final String REDIRECT_URL = "http://connect.mail.ru/oauth/success";

    /**
     * Request parameters keys
     */
    public static final String REQ_KEY_APP_ID = "app_id";
    public static final String REQ_KEY_METHOD = "method";
    public static final String REQ_KEY_SECURE = "secure";
    public static final String REQ_KEY_SESSION_KEY = "session_key";
    public static final String REQ_KEY_SIG = "sig";

    /**
     * Shared preferences keys
     */
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_SAVED_AT = "saved_at";
    public static final String KEY_ACCOUNT_NAME = "account_name";

    public static final boolean DEBUG = true;
}
