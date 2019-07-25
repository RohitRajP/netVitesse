package com.a011.netvitesse;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.a011.netvitesse.MainActivity.CHANNEL_ID;

public class VitesseService extends Service {

    public long lastRxBytes, lastTxBytes, beforeTime, afterTime;
    public static double downSpeed = 0.0, upSpeed = 0.0, dataUsed;
    DecimalFormat df = new DecimalFormat("#.##");
    ShutdownBroadcastReciever sBR = new ShutdownBroadcastReciever();

    Notification notification;
    @Override
    public void onCreate() {
        super.onCreate();


        // Registering broadcast receiver for system shutdown
        IntentFilter sysShutdownIntent = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(sBR, sysShutdownIntent);

        // gets the amount of packets received on service start
        lastRxBytes = TrafficStats.getTotalRxBytes();

        // gets the amount of packets sent on service start
        lastTxBytes = TrafficStats.getTotalTxBytes();

        // logging the current time to measure duration
        beforeTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        // unregistering broadcast receiver
        unregisterReceiver(sBR);
    }


    // used to update notifications
    public void setNotification(String connStatus, PendingIntent pendingIntent) {

        // notification template (converting speed into Mb/s on display
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(connStatus)
                .setContentText("↓" + downSpeed / 125 + " Mb/s" + " ↑" + upSpeed / 125 + " Mb/s")
                .setSmallIcon(R.drawable.ic_try2)
                .setVisibility(Notification.VISIBILITY_SECRET)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();

        // start foreground activity notification
        startForeground(1, notification);
    }


    // calculates speed of network
    public void calcSpeed(long timeTaken, long downBytes, long upBytes) {

        if (timeTaken > 0) {

            // calculating download and upload speed and converting into Mbps
            // using *1000 to convert from milliseconds to seconds
            downSpeed = (downBytes * 1000 / timeTaken) / 1000;
            upSpeed = (upBytes * 1000 / timeTaken) / 1000;
        }

    }


    // calculates the parameters required to calculate network speed
    public void calculateSpeedPrams() {

        // getting current RX and TX bytes
        long currentRxBytes = TrafficStats.getTotalRxBytes();
        long currentTxBytes = TrafficStats.getTotalTxBytes();

        // calculating the difference in RX and TX bytes
        long usedRxBytes = currentRxBytes - lastRxBytes;
        long usedTxBytes = currentTxBytes - lastTxBytes;

        // getting current time to help find time interval between
        // measurements
        afterTime = System.currentTimeMillis();

        // calculating time interval
        long usedTime = afterTime - beforeTime;

        // calling method to calculate speed on available data
        calcSpeed(usedTime, usedRxBytes, usedTxBytes);

        // updating values for use in next iteration
        lastRxBytes = currentRxBytes;
        lastTxBytes = currentTxBytes;
        beforeTime = afterTime;


    }


    // gets information on the network currently connected to and calculates data usage
    public String getConnectivityStatusString(Context context) {

        // creates a shared preference instance to fetch subscriberID
        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.a011.netvitesse", 0);

        // creating a NetworkStatsManager instance to fetch data usage statistics
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);

        // creating connectivity manager class instance
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // creating networkInfo instance
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // getting inConnected boolean to check for connectivity
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // if connected
        if (isConnected == true) {

            // creating a networkStats bucket to hold fetched values
            NetworkStats.Bucket bucket;

            // creating calendar instance to get the start of the day
            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getDefault());
            // navigating to current day
            c.add(Calendar.DAY_OF_MONTH, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            // getting current day beginning time as milliseconds
            long dayStartMilli = (c.getTimeInMillis());

            // check if connection is WiFi or Mobile Data
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

            // return appropriate connection details
            if (isWiFi == true) {


                try {
                    // fetching bucket with values
                    bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                            pref.getString("subscriberID", ""),
                            dayStartMilli,
                            System.currentTimeMillis());
                    // getting downloaded bytes of data
                    dataUsed = bucket.getRxBytes();
                    // converting data values to MB
                    dataUsed = (dataUsed) / (1024 * 1024);
                } catch (RemoteException e) {
                }

                // checking if data usage has crossed Gb checkpoint
                if (dataUsed > 999) {

                    // if it has, then show values in Gb and change text accordingly
                    dataUsed = dataUsed / 1024;
                    return "WiFi: " + df.format(dataUsed) + " GB";
                }

                // else just return MB value
                return "WiFi: " + df.format(dataUsed) + " MB";
            } else {

                try {
                    // fetching bucket with values
                    bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                            pref.getString("subscriberID", ""),
                            dayStartMilli,
                            System.currentTimeMillis());
                    // getting downloaded bytes of data
                    dataUsed = bucket.getRxBytes();
                    // converting data values to MB
                    dataUsed = (dataUsed) / (1024 * 1024);
                } catch (RemoteException e) {
                }

                // checking if data usage has crossed Gb checkpoint
                if (dataUsed > 999) {

                    // if it has, then show values in Gb and change text accordingly
                    dataUsed = dataUsed / 1024;
                    return "Mobile Data: " + df.format(dataUsed) + " GB ";
                }
                return "Mobile Data: " + df.format(dataUsed) + " MB ";
            }
        } else {
            return "Disconnected from internet";

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // setting up notification intent
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // creating initial notification
        setNotification("Setting this up...", pendingIntent);


        // starting thread to handle constant updation of notification values
        Thread t = new Thread() {

            @Override
            public void run() {
                // infinite loop
                while (true) {

                    // calculate speed of connection
                    calculateSpeedPrams();

                    // calling function to get connection status
                    String connStatus = getConnectivityStatusString(getApplicationContext());

                    // calling function to update notification
                    setNotification(connStatus, pendingIntent);

                    // pausing activity for 1 seconds
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        t.start();


        // the system will auto restart service if it is destroyed accidentally
        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
