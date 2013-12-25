package com.melnykov.dashclock.mailruextension;

import android.app.Application;
import android.content.Context;

public class MailRuExtensionApplication extends Application {

    private static Context context;

	@Override
    public void onCreate() {
		super.onCreate();
        MailRuExtensionApplication.context = getApplicationContext();
	}

    public static Context getContext() {
        return MailRuExtensionApplication.context;
    }
}