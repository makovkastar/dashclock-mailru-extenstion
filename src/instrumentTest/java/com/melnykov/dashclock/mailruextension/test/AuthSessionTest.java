package com.melnykov.dashclock.mailruextension.test;

import android.os.SystemClock;
import android.test.AndroidTestCase;
import com.melnykov.dashclock.mailruextension.Auth;
import com.melnykov.dashclock.mailruextension.Constants;
import com.melnykov.dashclock.mailruextension.Session;

import static org.fest.assertions.api.Assertions.assertThat;

public class AuthSessionTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = "b6442ed12223a7d0b459916b8ea03ce5";
    private static final String REFRESH_TOKEN = "b45529ac9bf6b32be761975c043ef9e3";
    private static final int EXPIRES_IN = 3;
    private static final String TOKEN_TYPE = "bearer";

    private static final String REDIRECT_URL = Constants.REDIRECT_URL +
            "#access_token=" + ACCESS_TOKEN +
            "&refresh_token=" + REFRESH_TOKEN +
            "&expires_in=" + EXPIRES_IN +
            "&token_type=" + TOKEN_TYPE;

    public void testParseRedirectUrl() {
        assertThat(Auth.getAccessToken(REDIRECT_URL)).isEqualTo(ACCESS_TOKEN);
        assertThat(Auth.getExpiresIn(REDIRECT_URL)).isEqualTo(EXPIRES_IN);
        assertThat(Auth.getRefreshToken(REDIRECT_URL)).isEqualTo(REFRESH_TOKEN);
    }

    public void testSessionInstance() {
        assertThat(Session.getInstance(getContext())).isNotNull();
        assertThat(Session.getInstance(getContext())).isEqualTo(Session.getInstance(getContext()));
    }

    public void testSaveDestroySession() {
        saveValidSession();
        assertThat(Session.getInstance(getContext()).isValid()).isTrue();

        Session oldSession = Session.getInstance(getContext());
        oldSession.destroy(getContext());

        assertThat(oldSession.isValid()).isFalse();
        assertThat(Session.getInstance(getContext()).isValid()).isFalse();
        assertThat(Session.getInstance(getContext())).isNotSameAs(oldSession);
    }

    public void testSessionData() {
        saveValidSession();

        assertThat(Session.getInstance(getContext()).getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(Session.getInstance(getContext()).getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

    public void testSessionExpiration() {
        saveExpiredSession();
        assertThat(Session.getInstance(getContext()).isValid()).isFalse();

        // Save valid session
        saveSession(EXPIRES_IN);
        assertThat(Session.getInstance(getContext()).isValid()).isTrue();
        // Wait until session expires
        SystemClock.sleep((EXPIRES_IN * 2) * 1000);
        assertThat(Session.getInstance(getContext()).isValid()).isFalse();
    }

    private void saveValidSession() {
        saveSession(EXPIRES_IN);
    }

    private void saveExpiredSession() {
        saveSession(0);
    }

    private void saveSession(int expiresIn) {
        Session.getInstance(getContext()).setAccessToken(ACCESS_TOKEN)
                .setRefreshToken(REFRESH_TOKEN)
                .setExpiresIn(expiresIn)
                .save(getContext());
    }
}
