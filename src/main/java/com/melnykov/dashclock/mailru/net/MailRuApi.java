package com.melnykov.dashclock.mailru.net;

import com.github.kevinsawicki.http.HttpRequest;
import com.melnykov.dashclock.mailru.Session;
import com.melnykov.dashclock.mailru.util.AuthUtil;
import com.melnykov.dashclock.mailru.util.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import java.util.TreeMap;

public class MailRuApi {

    // Parameters must be sorted in order to calculate request signature
    protected final TreeMap<String, String> params = new TreeMap<String, String>();

    private MailRuApi() {
    }

    private MailRuApi(String method) {
        params.put(Constants.REQ_KEY_METHOD, method);
        params.put(Constants.REQ_KEY_APP_ID, Constants.APP_ID);
        params.put(Constants.REQ_KEY_SECURE, String.valueOf(1));
        params.put(Constants.REQ_KEY_SESSION_KEY, Session.getInstance().getAccessToken());
        params.put(Constants.REQ_KEY_SIG, AuthUtil.calculateSignature(params, Constants.SECRET_KEY));
    }

    protected String sendGetRequest(String baseUrl) throws MailRuApiException {
        Timber.d("Sending GET request on %s with params: %s", baseUrl, params.toString());
        String response = HttpRequest.get(baseUrl, params, true).body();
        Timber.d("Response: %s", response);
        if (hasError(response)) {
            throw new MailRuApiException(getErrorCode(response));
        }
        return response;
    }

    protected String sendPostRequest(String baseUrl) throws MailRuApiException {
        Timber.d("Sending POST request on %s with params: %s", baseUrl, params.toString());
        String response = HttpRequest.post(baseUrl, params, true).body();
        Timber.d("Response: %s", response);
        if (hasError(response)) {
            throw new MailRuApiException(getErrorCode(response));
        }
        return response;
    }

    protected boolean hasError(String response) throws MailRuApiException {
        // Check for JSON array
        if(response.startsWith("[")) {
            return false;
        }
        // Check for HTML
        if (response.startsWith("<")) {
            throw new MailRuApiException(MailRuApiException.UNKNOWN_ERROR);
        }
        try {
            return new JSONObject(response).has("error");
        } catch (JSONException e) {
            // Cannot recover
            throw new RuntimeException(e);
        }
    }

    protected int getErrorCode(String response) {
        try {
            JSONObject json = new JSONObject(response);
            return json.getJSONObject("error").getInt("error_code");
        } catch (JSONException e) {
            // Cannot recover
            throw new RuntimeException(e);
        }
    }

    public static class UnreadMailCount extends MailRuApi {

        public UnreadMailCount() {
            super("mail.getUnreadCount");
        }

        public int get() throws MailRuApiException {
            String response = sendGetRequest(Constants.API_URL);
            try {
                return new JSONObject(response).getInt("count");
            } catch (JSONException e) {
                // Cannot recover
                throw new RuntimeException(e);
            }
        }
    }

    public static class AccessToken extends MailRuApi {

        public AccessToken() {
            params.put(Constants.REQ_KEY_GRANT_TYPE, "refresh_token");
            params.put(Constants.REQ_KEY_CLIENT_ID, Constants.APP_ID);
            params.put(Constants.REQ_KEY_CLIENT_SECRET, Constants.SECRET_KEY);
        }

        public Session refresh(String refreshToken) throws MailRuApiException {
            params.put(Constants.REQ_KEY_REFRESH_TOKEN, refreshToken);
            String response = sendPostRequest(Constants.REFRESH_TOKEN_URL);
            try {
                JSONObject json = new JSONObject(response);
                return Session.getInstance()
                        .setAccessToken(json.getString("access_token"), json.getInt("expires_in"))
                        .setRefreshToken(json.getString("refresh_token"));
            } catch (JSONException e) {
                // Cannot recover
                throw new RuntimeException(e);
            }
        }
    }
}
