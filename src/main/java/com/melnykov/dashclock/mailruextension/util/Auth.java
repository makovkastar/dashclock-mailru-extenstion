package com.melnykov.dashclock.mailruextension.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
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

    public static String calculateSignature(TreeMap<String, String> params, String secretKey) {
        StringBuilder sig = new StringBuilder();
        String paramStr = getParamsString(params);
        sig.append(paramStr).append(secretKey);
        return getMD5(sig.toString());
    }

    private static String getParamsString(TreeMap<String, String> params) {
        StringBuilder sortedParams = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sortedParams.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return sortedParams.toString();
    }

    private static String getMD5(String string) {
        String md5 = null;
        try {
            byte[] bytesOfMessage = string.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytesOfMessage);
            md5 = convertToHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return md5;
    }

    private static String convertToHexString(byte[] array) {
        StringBuilder hexStr = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            hexStr.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                    .substring(1, 3));
        }
        return hexStr.toString();
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
