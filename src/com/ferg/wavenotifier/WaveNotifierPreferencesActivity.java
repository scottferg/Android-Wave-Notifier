package com.ferg.wavenotifier;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class WaveNotifierPreferencesActivity extends PreferenceActivity {

    private static final String TAG = "WaveNotifier";
    private static final String EDIT_TEXT_USERNAME = "usernamePref";
    private static final String EDIT_TEXT_PASSWORD = "passwordPref";
    private static final WaveNotifierApplication mApplication = WaveNotifierApplication.getInstance();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.main);

        findPreference("usernamePref").setOnPreferenceChangeListener(credentialChanged);
        findPreference("passwordPref").setOnPreferenceChangeListener(credentialChanged);
    }

    Preference.OnPreferenceChangeListener credentialChanged = new Preference.OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference aPreference, Object aValue) {

            Log.i(TAG, aPreference.getKey() + ": " + (String) aValue);
            
            SharedPreferences prefs = WaveNotifierPreferencesActivity.this.getSharedPreferences("WaveNotifierPrefs", 0);
            Editor prefsEditor = prefs.edit();

            prefsEditor.putString(aPreference.getKey(), (String) aValue);
            prefsEditor.commit();

            // Start the service if it's not already started
            WaveNotifierApplication.getInstance().initNotifier(WaveNotifierPreferencesActivity.this);

			return true;
		}
	};
}
