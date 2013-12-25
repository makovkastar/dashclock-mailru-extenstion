package com.melnykov.dashclock.mailruextension.test;

import android.test.InstrumentationTestCase;
import com.github.kevinsawicki.http.HttpRequest;
import com.melnykov.dashclock.mailruextension.net.MailRuApiException;
import com.melnykov.dashclock.mailruextension.net.MailRuWebService;
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

public class MailRuWebServiceTest extends InstrumentationTestCase {

    private MockWebServer server = new MockWebServer();

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
        server.enqueue(new MockResponse().setBody(fileToString(R.raw.ok_response)));
        server.play();

        assertThat(new MailRuWebService.UnreadMailCount().get()).isEqualTo(1024);
    }

   public void testUnreadMailCountErrorResponse() throws IOException {
        server.enqueue(new MockResponse().setBody(fileToString(R.raw.error_response)));
        server.play();

        try {
            new MailRuWebService.UnreadMailCount().get();
            fail("Error response must throw api exception");
        } catch (MailRuApiException e) {
        }
    }

    private String fileToString(int resourceId) throws IOException {
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
