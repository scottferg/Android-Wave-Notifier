package com.ferg.wavenotifier;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.regex.*;
import java.util.Timer;
import java.util.TimerTask;

public class WaveNotifierService extends Service {

    private Timer mTimer;
    private NotificationManager mNotificationManager;

    private String mUsername;
    private String mPassword;

    // 30 second hard-coded refresh rate
    private static final int UPDATE_INTERVAL = 1800000;
    private static final String TAG = "WaveNotifierStatus";

    @Override
    public void onCreate() {

        super.onCreate();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mTimer = new Timer();

    }

    public int onStartCommand(Intent aIntent, int aFlags, int aStartId) {
        
        onStart(aIntent, aStartId);

        return 2;
    }

    @Override
    public void onStart(Intent aIntent, int aStartId) {

        mUsername = aIntent.getStringExtra("username");
        mPassword = aIntent.getStringExtra("password");

        runNotifier();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    public IBinder onBind(Intent aIntent) {

        return null;
    }

    private void runNotifier() {

        mTimer.scheduleAtFixedRate(
            new TimerTask() {
                public void run() {

                    getWaveUpdate();
                }
            },
            0,
            UPDATE_INTERVAL);
    }

    private void getWaveUpdate() {

        HashMap params = new HashMap();

        final Pattern errorPattern = Pattern.compile("Error=([A-z]+)");
        final Pattern authPattern = Pattern.compile("Auth=([A-z0-9_-]+)");
        final Pattern wavePattern = Pattern.compile("SID=([A-z0-9_-]+)");

        params.put("accountType", "GOOGLE");
        params.put("Email", mUsername);
        params.put("Passwd", mPassword);
        params.put("service", "wave");
        params.put("source", "wave-notifier-android");

        HTTPRequest request = new HTTPRequest("https://www.google.com/accounts/ClientLogin", "Content-Type", "application/x-www-form-urlencoded");
        String response = request.request(params);
        
        /*
        if (! errorPattern.matcher(response).equals(null)) {

            Log.i(TAG, "Whoops!");
        }
        */

        // TODO: If the credentials are bad, popup an error notification and
        // kill the service.  The service will restart when the user re-enters
        // their credentials.

        Matcher m = authPattern.matcher(response);
        String auth = "";
        String wave = "";

        while (m.find()) {
            auth = removePrefix(m.group());
        }

        m = wavePattern.matcher(response);

        while (m.find()) {
            wave = removePrefix(m.group());
        }
        
        Log.i(TAG, auth);
        Log.i(TAG, wave);

        params = new HashMap();

        params.put("nouacheck", "");
        params.put("auth", auth);

        request = new HTTPRequest("https://wave.google.com/wave/", "", "");
        response = request.request(params);

        Pattern waveGroup = Pattern.compile("var json = (\\{\"r\":\"\\^d1\".*\\});");
        final Pattern wavelet = Pattern.compile("\"7\":([0-9]+)");

        String inbox = "";

        m = waveGroup.matcher(response);

        Log.i(TAG, waveGroup.pattern());
        Log.i(TAG, String.valueOf(m.find()));

        inbox = m.group();

        m = wavelet.matcher(inbox);
        
        int result = 0;

        while (m.find()) {
            Log.i(TAG, m.group());

            String unreadCount = (m.group()).split(":")[1];

            Log.i(TAG, unreadCount);

            if (!unreadCount.equals("0")) {
                result++;
            }
        }

        if (result > 0) {
            notify(result, auth);
        } else {
            mNotificationManager.cancelAll();
        }
    }

    private void notify(int aCount, String aAuth) {

        long when = System.currentTimeMillis();
        CharSequence title = "Unread waves";
        CharSequence text = "You have " + Integer.toString(aCount) + " unread waves";

        Notification notification = new Notification(R.drawable.notification, title, when);
        
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wave.google.com/wave/?nouacheck&auth=" + aAuth));
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        notification.setLatestEventInfo(getApplicationContext(), title, text, contentIntent);
        notification.defaults |= Notification.DEFAULT_SOUND;

        mNotificationManager.notify(1, notification);
    }

    private String removePrefix(String aArg) {

        String[] chunk = null;

        chunk = aArg.split("=");

        return chunk[1];
    }
}
