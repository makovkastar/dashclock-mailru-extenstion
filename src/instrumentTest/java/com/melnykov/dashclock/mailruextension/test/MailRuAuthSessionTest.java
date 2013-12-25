package com.melnykov.dashclock.mailruextension.test;

import android.os.SystemClock;
import android.test.AndroidTestCase;
import com.melnykov.dashclock.mailruextension.net.MailRuAuth;
import com.melnykov.dashclock.mailruextension.util.Constants;
import com.melnykov.dashclock.mailruextension.Session;

import java.util.TreeMap;

import static org.fest.assertions.api.Assertions.assertThat;

public class MailRuAuthSessionTest extends AndroidTestCase {

    private static final String TEST_ACCESS_TOKEN = "b6442ed12223a7d0b459916b8ea03ce5";
    private static final String TEST_REFRESH_TOKEN = "b45529ac9bf6b32be761975c043ef9e3";
    private static final int TEST_EXPIRES_IN = 3;
    private static final String TEST_TOKEN_TYPE = "bearer";

    private static final String REDIRECT_URL = Constants.REDIRECT_URL +
            "#access_token=" + TEST_ACCESS_TOKEN +
            "&refresh_token=" + TEST_REFRESH_TOKEN +
            "&expires_in=" + TEST_EXPIRES_IN +
            "&token_type=" + TEST_TOKEN_TYPE;

    public void testParseRedirectUrl() {
        assertThat(MailRuAuth.getAccessToken(REDIRECT_URL)).isEqualTo(TEST_ACCESS_TOKEN);
        assertThat(MailRuAuth.getExpiresIn(REDIRECT_URL)).isEqualTo(TEST_EXPIRES_IN);
        assertThat(MailRuAuth.getRefreshToken(REDIRECT_URL)).isEqualTo(TEST_REFRESH_TOKEN);
    }

    public void testSessionInstance() {
        assertThat(Session.getInstance()).isNotNull();
        assertThat(Session.getInstance()).isEqualTo(Session.getInstance());
    }

    public void testSaveDestroySession() {
        saveValidSession();
        assertThat(Session.getInstance().isValid()).isTrue();

        Session oldSession = Session.getInstance();
        oldSession.destroy();

        assertThat(oldSession.isValid()).isFalse();
        assertThat(Session.getInstance().isValid()).isFalse();
        assertThat(Session.getInstance()).isNotSameAs(oldSession);
    }

    public void testSessionData() {
        saveValidSession();

        assertThat(Session.getInstance().getAccessToken()).isEqualTo(TEST_ACCESS_TOKEN);
        assertThat(Session.getInstance().getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);
    }

    public void testSessionExpiration() {
        // Save valid session
        saveValidSession();
        assertThat(Session.getInstance().isValid()).isTrue();
        // Wait until session expires
        SystemClock.sleep((TEST_EXPIRES_IN * 2) * 1000);
        assertThat(Session.getInstance().isValid()).isFalse();
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

        String sig = MailRuAuth.calculateSignature(params, testSecretKey);
        assertThat(sig).isEqualTo(expectedSig);
    }

    private void saveValidSession() {
        Session.getInstance().setAccessToken(TEST_ACCESS_TOKEN, TEST_EXPIRES_IN)
                .setRefreshToken(TEST_REFRESH_TOKEN)
                .save();
    }

}
