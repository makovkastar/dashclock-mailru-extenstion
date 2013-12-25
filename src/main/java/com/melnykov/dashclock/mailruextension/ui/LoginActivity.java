package com.melnykov.dashclock.mailruextension.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.melnykov.dashclock.mailruextension.R;
import com.melnykov.dashclock.mailruextension.Session;
import com.melnykov.dashclock.mailruextension.net.MailRuApiException;
import com.melnykov.dashclock.mailruextension.net.MailRuWebService;
import com.melnykov.dashclock.mailruextension.util.Auth;
import com.melnykov.dashclock.mailruextension.util.Constants;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final WebView wvLogin = (WebView) findViewById(R.id.wv_login);
        wvLogin.setWebViewClient(new VkWebViewClient());
        wvLogin.loadUrl(Auth.getAuthorizationUrl(Constants.APP_ID));
    }

    private class VkWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean result = true;
            if (url != null && url.startsWith(Constants.REDIRECT_URL)) {
                if (Constants.DEBUG) {
                    Log.v(TAG, "Redirect url : " + url);
                }

                if (url.contains("error")) {
                    showFailToast();
                } else {
                    saveSessionData(url);
                    showSuccessToast();
                }
                finish();
            } else {
                result = super.shouldOverrideUrlLoading(view, url);
            }

            return result;
        }

        private void saveSessionData(String url) {
            Session.getInstance()
                    .setAccessToken(Auth.getAccessToken(url), Auth.getExpiresIn(url))
                    .setRefreshToken(Auth.getRefreshToken(url))
                    .save();
            // Fetch user info
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        String email = new MailRuWebService.UserInfo().getEmail();
                        Session.getInstance().setAccountName(email).save();
                    } catch (MailRuApiException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
    }

    private void showSuccessToast() {
        Toast.makeText(this, getString(R.string.toast_login_success), Toast.LENGTH_SHORT).show();
    }

    private void showFailToast() {
        Toast.makeText(this, getString(R.string.toast_login_fail), Toast.LENGTH_SHORT).show();
    }
}

