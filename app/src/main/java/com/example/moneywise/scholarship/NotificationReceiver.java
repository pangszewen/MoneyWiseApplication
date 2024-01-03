package com.example.moneywise.scholarship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String scholarshipTitle = intent.getStringExtra("scholarshipTitle");

        // Send a local broadcast to notify ApplyScholarship
        Intent localIntent = new Intent("com.example.madassignment.scholarship.NOTIFICATION_RECEIVED");
        localIntent.putExtra("scholarshipTitle", scholarshipTitle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }
}

