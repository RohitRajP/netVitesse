package com.a011.netvitesse;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import static com.a011.netvitesse.MainActivity.CHANNEL_ID;

public class VitesseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static double downSpeed=0.0,upSpeed=0.0;
    static final String FILE_URL = "https://upload.wikimedia.org/wikipedia/en/9/9d/Orange-sun-small.jpg";
    static final long FILE_SIZE = 8; // 1kB in Kilobits

    // used to update notifications
    public void setNotification(String connStatus){
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(connStatus)
                .setContentText("Down - "+downSpeed+" KB/s")
                .setSmallIcon(R.drawable.ic_try2)
                .setOnlyAlertOnce(true)
                .build();
        startForeground(1,notification);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getConnectivityStatusString(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected == true){

                boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;


            String host = FILE_URL;
            long beforeTime = System.currentTimeMillis();

            getBitmapFromURL(host);

            long afterTime = System.currentTimeMillis();
            long timeDifference = afterTime - beforeTime;
            downSpeed = timeDifference/10;



            if(isWiFi == true){
                return "Connected - WiFi";
            }
            else{
                return "Connected - Mobile Data";
            }
        }
        else{
            return "Disconnected from internet";
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Creating initial notification
        setNotification("Setting this up...");

        // starting thread to handle constant updation of notification values
        Thread t = new Thread() {

            @Override
            public void run() {
                // infinite loop
                while (true){

                    // calling function to get connection status
                    String connStatus = getConnectivityStatusString(getApplicationContext());

                    // calling function to update notification
                    setNotification(connStatus);


                    // pausing activity for 5 seconds
                    try {

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        t.start();



        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
