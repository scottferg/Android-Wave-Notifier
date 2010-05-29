package com.ferg.wavenotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WaveNotifierReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context aContext, Intent aIntent) {

        WaveNotifierApplication.getInstance().initNotifier();
    }
}
