package com.melnykov.dashclock.mailruextension;

import android.content.Intent;
import android.util.Log;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.melnykov.dashclock.mailruextension.net.MailRuApiException;
import com.melnykov.dashclock.mailruextension.net.MailRuWebService;
import com.melnykov.dashclock.mailruextension.ui.LoginActivity;
import com.melnykov.dashclock.mailruextension.util.Constants;
import com.melnykov.dashclock.mailruextension.util.NetworkUtil;

public class MailRuExtension extends DashClockExtension {

    private static final String TAG = MailRuExtension.class.getSimpleName();

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int reason) {
        if (Constants.DEBUG) {
            Log.d(TAG, "Update data request. Reason: " + reason);
        }

        if (Session.getInstance().isAuthorized()) {
            if (NetworkUtil.hasInternetConnection(getApplicationContext())) {
                publishUpdate(buildActualExtensionData());
            }
        } else {
            publishUpdate(buildAuthRequiredExtensionData());
        }
    }

    private ExtensionData buildActualExtensionData() {
        ExtensionData extensionData = new ExtensionData();
        try {
            int unreadMailCount = new MailRuWebService.UnreadMailCount().get();
            if (unreadMailCount == 0) {
                // Hide extension if no new messages and no notifications
                extensionData.visible(false);
            } else {
                extensionData.status(formatQuantityString(R.plurals.unread_mails, unreadMailCount));
                extensionData.expandedBody(getAccountName());
                extensionData.clickIntent(createMessagesIntent());
                extensionData.visible(true);
            }
        } catch (Exception e) {
            // Hide extension if error occurred
            extensionData.visible(false);
            if (Constants.DEBUG) { Log.d(TAG, "Cannot build extension data", e); }
        }

        return extensionData;
    }

    private String getAccountName() throws MailRuApiException {
        if (Session.getInstance().getAccountName() == null) {
            String email = new MailRuWebService.UserInfo().getEmail();
            Session.getInstance().setAccountName(email).save();
            return email;
        } else {
            return Session.getInstance().getAccountName();
        }
    }

    private String formatQuantityString(int resId, int quantity) {
        return getResources().getQuantityString(resId, quantity, quantity);
    }

    private Intent createMessagesIntent() {
        return null;
    }

    private ExtensionData buildAuthRequiredExtensionData() {
        ExtensionData extensionData = new ExtensionData().visible(true);
        extensionData.status(getString(R.string.authorization_needed_status))
                .expandedBody(getString(R.string.authorization_needed_body))
                .clickIntent(createLoginIntent());

        return extensionData;
    }

    private Intent createLoginIntent() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        return intent;
    }
}
