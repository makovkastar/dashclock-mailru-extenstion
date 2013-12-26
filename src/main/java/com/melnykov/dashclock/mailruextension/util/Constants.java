package com.melnykov.dashclock.mailruextension.util;

public class Constants {
    public static final String API_URL = "http://www.appsmail.ru/platform/api";
    public static final String APP_ID = "705871";
    public static final String AUTH_URL = "https://connect.mail.ru/oauth/authorize";
    public static final String REDIRECT_URL = "http://connect.mail.ru/oauth/success";
    public static final String REFRESH_TOKEN_URL = "http://www.appsmail.ru//oauth/token";
    public static final String SECRET_KEY = "";

    /**
     * Request parameters keys
     */
    public static final String REQ_KEY_APP_ID = "app_id";
    public static final String REQ_KEY_CLIENT_ID = "client_id";
    public static final String REQ_KEY_CLIENT_SECRET = "client_secret";
    public static final String REQ_KEY_GRANT_TYPE = "grant_type";
    public static final String REQ_KEY_METHOD = "method";
    public static final String REQ_KEY_SECURE = "secure";
    public static final String REQ_KEY_SESSION_KEY = "session_key";
    public static final String REQ_KEY_SIG = "sig";
    public static final String REQ_KEY_REFRESH_TOKEN = "refresh_token";

    /**
     * Shared preferences keys
     */
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_SAVED_AT = "saved_at";

    public static final boolean DEBUG = true;
}
