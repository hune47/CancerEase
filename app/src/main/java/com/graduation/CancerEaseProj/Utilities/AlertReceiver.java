package com.graduation.CancerEaseProj.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import java.util.Random;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb;
        if (intent.hasExtra("video")){
            String video = intent.getStringExtra("video");
            nb = notificationHelper.getChannel_3_Notification(title, message, video);
        }else if (intent.hasExtra("url")){
            String url = intent.getStringExtra("url");
            nb = notificationHelper.getChannel_3_Notification(title, message, url);
        } else {
            nb = notificationHelper.getChannel_2_Notification(title,message);
        }
        notificationHelper.getManager().notify(new Random().nextInt(), nb.build());
    }
}
