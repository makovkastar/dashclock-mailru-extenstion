package com.melnykov.dashclock.mailruextension.test;

import android.test.AndroidTestCase;
import com.melnykov.dashclock.mailruextension.Auth;
import com.melnykov.dashclock.mailruextension.Constants;

import static org.fest.assertions.api.Assertions.assertThat;

public class AuthSessionTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = "b6442ed12223a7d0b459916b8ea03ce5";
    private static final String REFRESH_TOKEN = "b45529ac9bf6b32be761975c043ef9e3";
    private static final int EXPIRES_IN = 3600;
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
}
