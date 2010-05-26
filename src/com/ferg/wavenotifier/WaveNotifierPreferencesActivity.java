package com.ferg.wavenotifier;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

public class WaveNotifierPreferencesActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startService(new Intent(this, WaveNotifierService.class));
    }
}
