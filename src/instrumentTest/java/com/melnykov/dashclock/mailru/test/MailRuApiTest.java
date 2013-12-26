package com.melnykov.dashclock.mailru.test;

import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import com.github.kevinsawicki.http.HttpRequest;
import com.melnykov.dashclock.mailru.Session;
import com.melnykov.dashclock.mailru.net.MailRuApi;
import com.melnykov.dashclock.mailru.net.MailRuApiException;
import com.melnykov.dashclock.mailruextension.test.R;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

import static org.fest.assertions.api.Assertions.assertThat;

public class MailRuApiTest extends InstrumentationTestCase {

    protected MockWebServer server = new MockWebServer();

    public void setUp() {
        HttpRequest.setConnectionFactory(new HttpRequest.ConnectionFactory() {
            @Override
            public HttpURLConnection create(URL url) throws IOException {
                return (HttpURLConnection) server.getUrl(url.getPath()).openConnection();
            }

            @Override
            public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
                return (HttpURLConnection) server.getUrl(url.getPath()).openConnection(proxy);
            }
        });
    }

    public void testUnreadMailCountOkResponse() throws Exception {
        server.enqueue(new MockResponse().setBody(fileToString(R.raw.unread_mail_count_ok_response)));
        server.play();

        assertThat(new MailRuApi.UnreadMailCount().get()).isEqualTo(1024);
    }

   public void testUnreadMailCountErrorResponse() throws IOException {
        server.enqueue(new MockResponse().setBody(fileToString(R.raw.error_response)));
        server.play();

        try {
            new MailRuApi.UnreadMailCount().get();
            fail("Error response must throw api exception");
        } catch (MailRuApiException e) {
        }
    }

    public void testRefreshAccessToken() throws Exception {
        server.enqueue(new MockResponse().setBody(fileToString(R.raw.access_token_refresh_ok_response)));
        server.play();

        Session.getInstance().setAccessToken(TestConstants.ACCESS_TOKEN, TestConstants.EXPIRES_IN)
                .setRefreshToken(TestConstants.REFRESH_TOKEN)
                .save();

        assertThat(Session.getInstance().isValid()).isTrue();
        // Wait until session expires
        SystemClock.sleep((TestConstants.EXPIRES_IN * 2) * 1000);
        assertThat(Session.getInstance().isValid()).isFalse();
        // Refresh access token
        new MailRuApi.AccessToken().refresh(Session.getInstance().getRefreshToken()).save();
        assertThat(Session.getInstance().getAccessToken()).isEqualTo(TestConstants.REFRESHED_ACCESS_TOKEN);
        assertThat(Session.getInstance().isValid()).isTrue();
    }

    protected String fileToString(int resourceId) throws IOException {
        InputStream is = getInstrumentation().getContext().getResources().openRawResource(resourceId);
        return convertInputStreamToString(is);
    }

    private String convertInputStreamToString(InputStream is) throws IOException{
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
}
