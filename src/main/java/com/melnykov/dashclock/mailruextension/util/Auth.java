package com.melnykov.dashclock.mailruextension.util;

import com.melnykov.dashclock.mailruextension.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Auth {

    public static String getAuthorizationUrl(String appId) {
        return new StringBuilder(Constants.AUTH_URL)
                .append("?client_id=").append(appId)
                .append("&response_type=token")
                .append("&display=mobile")
                .append("&redirect_uri=").append(Constants.REDIRECT_URL)
                .toString();
    }

    public static String getAccessToken(String url) {
        return extractPattern(url, "access_token=(.*?)(&|$)", 1);
    }

    public static int getExpiresIn(String url) {
        String expiresIn = extractPattern(url, "expires_in=([0-9]+)(&|$)", 1);
        if (expiresIn != null) {
            return Integer.parseInt(expiresIn);
        } else {
            return 0;
        }
    }

    public static String getRefreshToken(String url) {
        return extractPattern(url, "refresh_token=(.*?)(&|$)", 1);
    }

    private static String extractPattern(String string, String pattern, int group) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find()) {
            return m.group(group);
        } else {
            return null;
        }
    }
}
