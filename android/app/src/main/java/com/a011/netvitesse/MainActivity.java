package com.a011.netvitesse;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

          // getting service status
          //boolean serviceStatus = isMyServiceRunning(VitesseService.class);

          // returning appropriate result
//          if(serviceStatus == true)
//            result.success("Service Started");
//          else
//            result.success("Service Failed");
        }
        else if (methodCall.method.equals("stopService")) {

          // stopping service
          stopService();

          // returning confirmation message
          result.success("Service Cancelled");

        }
        else if (methodCall.method.equals("checkStatus")) {

          // checking service status
          boolean serviceStatus = isMyServiceRunning(VitesseService.class);

          // returning appropriate result
          if (serviceStatus == true)
            result.success("Service Running");
          else
            result.success("Service Stopped");
        }
        else if (methodCall.method.equals("openGit")) {

          // intent browser to repository
          Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RohitRajP/netVitesse"));
          startActivity(gitIntent);

        }
        else if (methodCall.method.equals("checkUsagePermission")) {
          result.success(checkForPermission(getApplicationContext()));
        }
        else if (methodCall.method.equals("getUsageAccessPermission")) {
          startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
      }
    });

  }

  // check for permission for usage access
  private boolean checkForPermission(Context context) {

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



  // starts foreground service
  public void startService() {


    TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      ActivityCompat.requestPermissions(MainActivity.this,
              new String[]{Manifest.permission.READ_PHONE_STATE},
              0);
      return;
    }
    String subscriberId = manager.getSubscriberId();
    NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
    NetworkStats.Bucket bucket;
    Calendar c = Calendar.getInstance();
    c.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
    c.add(Calendar.DAY_OF_MONTH, 0);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    long howMany = (c.getTimeInMillis());

//    long hours = TimeUnit.MILLISECONDS.toHours(howMany);
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    try {
      bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
              subscriberId,
              howMany,
              System.currentTimeMillis());
      long bytes = bucket.getRxBytes();
      bytes = (bytes)/(1024*1024);
      Toast.makeText(this, "TotalRX: "+(double)bytes/1000+" TotalTX: ", Toast.LENGTH_SHORT).show();
    } catch (RemoteException e) {
    }

//    Intent serviceIntent = new Intent(this, VitesseService.class);
//      startService(serviceIntent);


  }

  // stops foreground service
  public void stopService(){
    Intent serviceIntent = new Intent(this, VitesseService.class);
    stopService(serviceIntent);
  }

  // creates notification channel
  private void createNotificationChannel(){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

      NotificationChannel serviceChannel = new NotificationChannel(
              CHANNEL_ID,
              "NetVitesse Notification",
              NotificationManager.IMPORTANCE_DEFAULT
      );

      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(serviceChannel);

    }
  }

}
