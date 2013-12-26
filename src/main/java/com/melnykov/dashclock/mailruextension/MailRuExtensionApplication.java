package com.melnykov.dashclock.mailruextension;

import android.app.Application;
import android.content.Context;
import timber.log.Timber;

public class MailRuExtensionApplication extends Application {

    private static Context context;

	@Override
    public void onCreate() {
		super.onCreate();
        MailRuExtensionApplication.context = getApplicationContext();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
	}

    public static Context getContext() {
        return MailRuExtensionApplication.context;
    }
}