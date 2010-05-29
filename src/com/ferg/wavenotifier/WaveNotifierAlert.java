package com.ferg.wavenotifier;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

public class WaveNotifierAlert {

	static final int WARNING = 0;
	static final int ERROR = 1;
	static final int SCROBBLE = 2;
	static final int CUSTOM = 3;

    static final void notify(Context aContext, int aType) { 

        NotificationManager mNotificationManager = (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = 0;
        String sType = "";

        switch(aType) {
            case ERROR:
                icon = android.R.drawable.stat_notify_error;
                sType = " - ERROR";
                break;
            default:
                icon = android.R.drawable.stat_notify_error;

        }

        // Occurs if the connection was never initialized
        CharSequence notificationText = "Wave Notifier";


        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, notificationText, when);

        CharSequence title = "Wave Notifier" + sType;
        CharSequence text = "Wave Notifier was unable to connect. Did you enter your username and password correctly?";

        Intent notificationIntent = new Intent(aContext.getApplicationContext(), WaveNotifierPreferencesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(aContext.getApplicationContext(), 0, notificationIntent, 0);

        notification.setLatestEventInfo(aContext.getApplicationContext(), title, text, contentIntent);

        mNotificationManager.notify(1, notification);
    }
}
