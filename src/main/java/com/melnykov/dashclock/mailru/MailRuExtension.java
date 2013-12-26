package com.melnykov.dashclock.mailru;

import android.content.ComponentName;
import android.content.Intent;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.melnykov.dashclock.mailru.net.MailRuApi;
import com.melnykov.dashclock.mailru.net.MailRuApiException;
import com.melnykov.dashclock.mailru.ui.LoginActivity;
import com.melnykov.dashclock.mailru.util.NetworkUtil;
import timber.log.Timber;

public class MailRuExtension extends DashClockExtension {

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int reason) {
        Timber.d("Update data request. Reason: %d.", reason);

        if (Session.getInstance().isAuthorized()) {
            if (NetworkUtil.hasInternetConnection(getApplicationContext())) {
                publishUpdate(buildActualExtensionData());
            } else {
                // Don't touch extension if there's not internet connection
                Timber.d("No internet connection. Skipping an update.");
            }
        } else {
            publishUpdate(buildAuthRequiredExtensionData());
        }
    }

    private ExtensionData buildActualExtensionData() {
        ExtensionData extensionData = buildBasicExtensionData();
        try {
            if (!Session.getInstance().isValid()) {
                Timber.d("Session expired. Refreshing access token...");
                refreshAccessToken();
            }
            int unreadMailCount = new MailRuApi.UnreadMailCount().get();
            if (unreadMailCount == 0) {
                // Hide extension if no new messages and no notifications
                extensionData.visible(false);
            } else {
                extensionData.status(formatQuantityString(R.plurals.unread_mails, unreadMailCount));
                extensionData.expandedBody(getString(R.string.extension_title));
                extensionData.clickIntent(createMessagesIntent());
                extensionData.visible(true);
            }
        } catch (Exception e) {
            // Hide extension if error occurred
            extensionData.visible(false);
            Timber.e(e, "Cannot build extension data.");
        }

        return extensionData;
    }

    private void refreshAccessToken() throws MailRuApiException {
        Session session = new MailRuApi.AccessToken().refresh(Session.getInstance().getRefreshToken());
        session.save();
    }

    private String formatQuantityString(int resId, int quantity) {
        return getResources().getQuantityString(resId, quantity, quantity);
    }

    private Intent createMessagesIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("ru.mail.mailapp", "ru.mail.ui.SlideStackActivity"));
        return intent;
    }

    private ExtensionData buildAuthRequiredExtensionData() {
        ExtensionData extensionData = buildBasicExtensionData();
        extensionData.status(getString(R.string.authorization_needed_status))
                .expandedBody(getString(R.string.authorization_needed_body))
                .clickIntent(createLoginIntent());

        return extensionData;
    }

    private ExtensionData buildBasicExtensionData() {
        return new ExtensionData().visible(true).icon(R.drawable.ic_extension);
    }

    private Intent createLoginIntent() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        return intent;
    }
}
