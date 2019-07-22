package com.a011.netvitesse;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

  public static final String CHANNEL_ID = "com.a011.netvitesse";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    // creating notification channel
    createNotificationChannel();

    // setting method channels to communicate with flutter
    new MethodChannel(getFlutterView(),CHANNEL_ID).setMethodCallHandler(new MethodChannel.MethodCallHandler() {
      @Override
      public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {

        // checking if start service is called
        if(methodCall.method.equals("startService")){

          // starting service
          startService();

          // getting service status
          boolean serviceStatus = isMyServiceRunning(VitesseService.class);

          // returning appropriate result
          if(serviceStatus == true)
            result.success("Service Started");
          else
            result.success("Service Failed");

        }
        else if(methodCall.method.equals("stopService")){

          // stopping service
          stopService();

          // returning confirmation message
          result.success("Service Cancelled");

        }
        else if(methodCall.method.equals("checkStatus")){

          // checking service status
          boolean serviceStatus = isMyServiceRunning(VitesseService.class);

          // returning appropriate result
          if(serviceStatus == true)
            result.success("Service Running");
          else
            result.success("Service Stopped");
        }
        else if(methodCall.method.equals("openGit")){

          // intent browser to repository
          Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RohitRajP/netVitesse"));
          startActivity(gitIntent);

        }
      }
    });

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
  public void startService(){
    Intent serviceIntent = new Intent(this, VitesseService.class);
    startService(serviceIntent);
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
