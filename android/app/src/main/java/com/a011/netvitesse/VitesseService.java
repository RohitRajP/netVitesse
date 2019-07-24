package com.a011.netvitesse;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        // the system will auto restart service if it is destroyed accidentally
        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
