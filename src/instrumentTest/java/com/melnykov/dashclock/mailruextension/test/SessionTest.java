package com.melnykov.dashclock.mailruextension.test;

import android.test.InstrumentationTestCase;
import com.melnykov.dashclock.mailruextension.Session;

import static org.fest.assertions.api.Assertions.assertThat;

public class SessionTest extends InstrumentationTestCase {

    public void setUp() {
        destroyCurrentSession();
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

        assertThat(Session.getInstance().getAccessToken()).isEqualTo(TestConstants.ACCESS_TOKEN);
        assertThat(Session.getInstance().getRefreshToken()).isEqualTo(TestConstants.REFRESH_TOKEN);
    }

    private void destroyCurrentSession() {
        Session.getInstance().destroy();
    }

    private void saveValidSession() {
        Session.getInstance().setAccessToken(TestConstants.ACCESS_TOKEN, TestConstants.EXPIRES_IN)
                .setRefreshToken(TestConstants.REFRESH_TOKEN)
                .save();
    }
}
