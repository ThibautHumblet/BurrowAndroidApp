package com.example.mobdev_project.Helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mobdev_project.R;

public class NotificationPublisher extends BroadcastReceiver {
    public NotificationPublisher() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        //NEED A RESCHEDULE?
        Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();

        updateNotification(context);
        Log.d("Not", "Hello");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateNotification(Context context){
        Notification notification = new Notification.Builder(context.getApplicationContext(), "default")
                .setContentTitle("title")
                .setContentText("body")
                .setSmallIcon(R.drawable.burrow_logo)
                .setAutoCancel(true)
                .build();
        NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager!=null) manager.notify(123, notification);
    }
}