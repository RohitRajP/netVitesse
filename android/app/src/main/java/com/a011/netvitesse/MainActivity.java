package com.a011.netvitesse;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

import static android.support.v4.app.AppOpsManagerCompat.MODE_ALLOWED;

public class MainActivity extends FlutterActivity {

    public static final String CHANNEL_ID = "com.a011.netvitesse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        // creating notification channel
        createNotificationChannel();

        // setting method channels to communicate with flutter
        new MethodChannel(getFlutterView(), CHANNEL_ID).setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {

                // checking if start service is called
                if (methodCall.method.equals("startService")) {

                    // starting service
                    startService();

                    //getting service status
                    boolean serviceStatus = isMyServiceRunning(VitesseService.class);
                    // returning appropriate result
                    if (serviceStatus == true)
                        result.success("Service Started");
                    else
                        result.success("Service Failed");
                }
                // checking if stop service is called
                else if (methodCall.method.equals("stopService")) {

                    // stopping service
                    stopService();

                    // returning confirmation message
                    result.success("Service Cancelled");

                } else if (methodCall.method.equals("checkStatus")) {

                    // checking service status
                    boolean serviceStatus = isMyServiceRunning(VitesseService.class);

                    // returning appropriate result
                    if (serviceStatus == true)
                        result.success("Service Running");
                    else
                        result.success("Service Stopped");
                } else if (methodCall.method.equals("openGit")) {

                    // intent browser to repository
                    Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RohitRajP/netVitesse"));
                    startActivity(gitIntent);

                } else if (methodCall.method.equals("checkUsagePermission")) {
                    result.success(checkUsageAccessForPermission(getApplicationContext()));
                }
                // getting usage access permission from user
                else if (methodCall.method.equals("getUsageAccessPermission")) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
                // getting PHONE_STATE permission from user
                else if (methodCall.method.equals("getPhoneStatePermission")) {
                    result.success(getPhoneStatePermission());
                }
                // starts ScheduleJob
                else if (methodCall.method.equals("startJob")) {
                    scheduleJob();
                }
            }
        });

    }

    private void scheduleJob() {


    }

    // gets the phone state permission and stores the subscriber ID in shared Preferences
    private boolean getPhoneStatePermission() {

        // checking for and getting TelephonyManager permission
        TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    0);
            return true;
        }

        // getting subscriberID from phone
        String subscriberId = manager.getSubscriberId();

        // creating an instance of sharedPreference
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.a011.netvitesse", 0);
        // creating an instance of sharedPreference Editor
        SharedPreferences.Editor editor = prefs.edit();

        // inserting subscriberID into sharedPreferences
        editor.putString("subscriberID", subscriberId);
        editor.commit();
        return true;

    }

    // check for permission for usage access
    private boolean checkUsageAccessForPermission(Context context) {

        // getting the application UID
        ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int uid = info.uid;

        // checking if application has usage access
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, uid, context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    // checks if service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // checks if job is scheduled
    public static boolean isJobIdRunning(Context context, int JobId) {
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JobId) {
                return true;
            }
        }

        return false;
    }


    // starts foreground service
    public void startService() {
        // starting thread to start service

        Intent serviceIntent = new Intent(getApplicationContext(), VitesseService.class);
        startService(serviceIntent);


    }

    // stops foreground service
    public void stopService() {
        Intent serviceIntent = new Intent(this, VitesseService.class);
        stopService(serviceIntent);
    }

    // creates notification channel
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "NetVitesse Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }

}
