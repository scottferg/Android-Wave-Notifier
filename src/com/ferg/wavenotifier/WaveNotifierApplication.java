package com.ferg.wavenotifier;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

public class WaveNotifierApplication extends Application {

    public static WaveNotifierApplication sInstance = null;
    private static final String TAG = "WaveNotifierApplication";
    
    public static WaveNotifierApplication getInstance() {

        if (sInstance != null) {
            return sInstance;
        } else {
            return new WaveNotifierApplication();
        }
    }

    @Override
    public void onCreate() {

        sInstance = this;

        initNotifier();
    }

    public void initNotifier() {

        SharedPreferences settings = getSharedPreferences("WaveNotifierPrefs", 0);
        String username = settings.getString("usernamePref", "");
        String password = settings.getString("passwordPref", "");

        Log.i(TAG, username);
        Log.i(TAG, password);

        Intent service = new Intent(this, WaveNotifierService.class);
        service.putExtra("username", username);
        service.putExtra("password", password);

        startService(service);
    }
}
