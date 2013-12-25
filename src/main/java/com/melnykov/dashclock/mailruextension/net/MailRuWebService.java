package com.melnykov.dashclock.mailruextension.net;

import com.github.kevinsawicki.http.HttpRequest;
import com.melnykov.dashclock.mailruextension.Session;
import com.melnykov.dashclock.mailruextension.util.Auth;
import com.melnykov.dashclock.mailruextension.util.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

public class MailRuWebService {

    private static final String TAG = MailRuWebService.class.getSimpleName();

    private static final String BASE_URL = "http://www.appsmail.ru/platform/api";
    // Parameters must be sorted in order to calculate request signature
    private final TreeMap<String, String> params = new TreeMap<String, String>();

    public MailRuWebService(String method) {
        params.put(Constants.REQ_KEY_METHOD, method);
        params.put(Constants.REQ_KEY_APP_ID, Constants.APP_ID);
        params.put(Constants.REQ_KEY_SECURE, String.valueOf(1));
        params.put(Constants.REQ_KEY_SESSION_KEY, Session.getInstance().getAccessToken());
        params.put(Constants.REQ_KEY_SIG, Auth.calculateSignature(params, Constants.APP_SECRET_KEY));
    }

    public String sendRequest() throws MailRuApiException {
        String response = HttpRequest.get(BASE_URL, params, true).body();
        if (hasError(response)) {
            throw new MailRuApiException(getErrorCode(response));
        }
        return response;
    }

    private boolean hasError(String response) {
        try {
            return new JSONObject(response).has("error");
        } catch (JSONException e) {
            // Cannot recover
            throw new RuntimeException(e);
        }
    }

    private int getErrorCode(String response) {
        try {
            JSONObject json = new JSONObject(response);
            return json.getJSONObject("error").getInt("error_code");
        } catch (JSONException e) {
            // Cannot recover
            throw new RuntimeException(e);
        }
    }

    public static class UnreadMailCount extends MailRuWebService {

        public UnreadMailCount() {
            super("mail.getUnreadCount");
        }

        public int get() throws MailRuApiException {
            String response = sendRequest();
            try {
                return new JSONObject(response).getInt("count");
            } catch (JSONException e) {
                // Cannot recover
                throw new RuntimeException(e);
            }
        }
    }
}
