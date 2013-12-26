package com.melnykov.dashclock.mailru.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.melnykov.dashclock.mailru.R;
import com.melnykov.dashclock.mailru.Session;
import com.melnykov.dashclock.mailru.util.Constants;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Session.getInstance().isAuthorized()) {
            setupAuthorizedPreferences();
        } else {
            setupLoginPreferences();
        }
    }

    private void setupAuthorizedPreferences() {
        addPreferencesFromResource(R.xml.preferences);
        Preference rateAppPref = (Preference) findPreference(getString(R.string.key_rate_app));
        rateAppPref
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Constants.MARKET_URI));
                        startActivity(intent);
                        return true;
                    }
                });
    }

    private void setupLoginPreferences() {
        addPreferencesFromResource(R.xml.login_preferences);
        Preference logInPref = findPreference(getString(R.string.key_log_in));
        logInPref
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startLoginActivity();
                        getActivity().finish();
                        return true;
                    }
                });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
