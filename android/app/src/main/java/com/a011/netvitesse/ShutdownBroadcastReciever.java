package com.a011.netvitesse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.util.Log;
import android.widget.Toast;

public class ShutdownBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_SHUTDOWN.equals(intent.getAction()) || Intent.ACTION_REBOOT.equals(intent.getAction())){

            // getting sharedPreferences instance referring to this application
            SharedPreferences prefs = context.getSharedPreferences("com.a011.netvitesse",0);
            // setting up sharedPreference editor to insert data
            SharedPreferences.Editor editor = prefs.edit();

            // getting current RX and TX bytes
            long currentRxBytes = TrafficStats.getTotalRxBytes();
            long currentTxBytes = TrafficStats.getTotalTxBytes();
            long currentMobileRxBytes = TrafficStats.getMobileRxBytes();
            long currentMobileTxBytes = TrafficStats.getMobileTxBytes();

            // inserting values into sharedPreferences
            editor.putLong("totalRXBytes",currentRxBytes);
            editor.putLong("totalTXBytes",currentTxBytes);
            editor.putLong("mobileRXBytes",currentMobileRxBytes);
            editor.putLong("mobileTXBytes",currentMobileTxBytes);
            editor.putInt("valueReset",1);

            Log.e("ShutdownBR", "Writing TotalRXBytes: " +currentRxBytes);
            Log.e("ShutdownBR", "Writing TotalTXBytes: " +currentTxBytes);
            Log.e("ShutdownBR", "Writing TotalMobileRXBytes: " +currentMobileRxBytes);
            Log.e("ShutdownBR", "Writing TotalMobileTXBytes: " +currentMobileTxBytes);
            Log.e("ShutdownBR", "ValueReset set to 1");


            // commiting all changes
            editor.commit();

            Log.e("ShutdownBR","Shutting Down");

        }
        else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            Toast.makeText(context, "Connection Changed", Toast.LENGTH_SHORT).show();
        }
        else if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Toast.makeText(context, "System Booted", Toast.LENGTH_SHORT).show();
        }
    }
}
