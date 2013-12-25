package com.melnykov.dashclock.mailruextension;

import android.content.Intent;
import android.util.Log;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.melnykov.dashclock.mailruextension.util.Constants;

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
            publishUpdate(buildActualExtensionData());
        } else {
            publishUpdate(buildAuthRequiredExtensionData());
        }
    }

    private ExtensionData buildActualExtensionData() {
        ExtensionData extensionData = new ExtensionData();
        int unreadMailCount = new MailRuWebService.UnreadMailCount().get();
        try {
            if (unreadMailCount == 0) {
                // Hide extension if no new messages and no notifications
                extensionData.visible(false);
            } else {
                extensionData.visible(true);
                String status = formatQuantityString(R.plurals.unread_mails, unreadMailCount);
                String expandedBody = "";

                extensionData.status(status);
                extensionData.expandedBody(expandedBody);
                extensionData.clickIntent(createMessagesIntent());
            }
        } catch (Exception e) {
            e.printStackTrace();
            extensionData.visible(false);
        }

        return extensionData;
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
