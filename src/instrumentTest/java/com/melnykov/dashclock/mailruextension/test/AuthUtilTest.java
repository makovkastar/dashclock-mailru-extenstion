package com.melnykov.dashclock.mailruextension.test;

import android.test.AndroidTestCase;
import com.melnykov.dashclock.mailruextension.util.AuthUtil;
import com.melnykov.dashclock.mailruextension.util.Constants;

import java.util.TreeMap;

import static org.fest.assertions.api.Assertions.assertThat;

public class AuthUtilTest extends AndroidTestCase {

    private static final String REDIRECT_SUCCESS_URL = Constants.REDIRECT_URL +
            "#access_token=" + TestConstants.ACCESS_TOKEN +
            "&refresh_token=" + TestConstants.REFRESH_TOKEN +
            "&expires_in=" + TestConstants.EXPIRES_IN +
            "&token_type=bearer";

    public void testParseRedirectUrl() {
        assertThat(AuthUtil.getAccessToken(REDIRECT_SUCCESS_URL)).isEqualTo(TestConstants.ACCESS_TOKEN);
        assertThat(AuthUtil.getRefreshToken(REDIRECT_SUCCESS_URL)).isEqualTo(TestConstants.REFRESH_TOKEN);
        assertThat(AuthUtil.getExpiresIn(REDIRECT_SUCCESS_URL)).isEqualTo(TestConstants.EXPIRES_IN);
    }

    public void testCalculateSig() {
        // Example from http://api.mail.ru/docs/guides/restapi/#server
        // Request: http://www.appsmail.ru/platform/api?method=friends.get&app_id=423004&session_key=be6ef89965d58e56dec21acb9b62bdaa&secure=1
        // Secret key: 3dad9cbf9baaa0360c0f2ba372d25716
        // sig = md5(app_id=423004method=friends.getsecure=1session_key=be6ef89965d58e56dec21acb9b62bdaa3dad9cbf9baaa0360c0f2ba372d25716)
        // = 4a05af66f80da18b308fa7e536912bae
        final String testSecretKey = "3dad9cbf9baaa0360c0f2ba372d25716";
        final String expectedSig = "4a05af66f80da18b308fa7e536912bae";

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put(Constants.REQ_KEY_METHOD, "friends.get");
        params.put(Constants.REQ_KEY_APP_ID, "423004");
        params.put(Constants.REQ_KEY_SESSION_KEY, "be6ef89965d58e56dec21acb9b62bdaa");
        params.put(Constants.REQ_KEY_SECURE, "1");

        String sig = AuthUtil.calculateSignature(params, testSecretKey);
        assertThat(sig).isEqualTo(expectedSig);
    }
}
