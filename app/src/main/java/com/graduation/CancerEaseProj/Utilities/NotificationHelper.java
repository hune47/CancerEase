package com.graduation.CancerEaseProj.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.graduation.CancerEaseProj.Activities.EducationActivity;
import com.graduation.CancerEaseProj.Activities.WelcomeActivity;
import com.graduation.CancerEaseProj.R;

public class NotificationHelper extends ContextWrapper {
    public static final String CHANNEL_1_ID = "channel_1_id";
    public static final String CHANNEL_1_NAME = "channel_1";
    public static final String CHANNEL_2_ID = "channel_2_id";
    public static final String CHANNEL_2_NAME = "channel_2";
    public static final String CHANNEL_3_ID = "channel_3_id";
    public static final String CHANNEL_3_NAME = "channel_3";
    private NotificationManager mManager;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void  createChannels() {
        NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, CHANNEL_1_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.colorPrimary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel1);

        NotificationChannel channel2 = new NotificationChannel(CHANNEL_2_ID, CHANNEL_2_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLightColor(R.color.colorPrimary);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel2);

        NotificationChannel channel3 = new NotificationChannel(CHANNEL_3_ID, CHANNEL_3_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel3.enableLights(true);
        channel3.enableVibration(true);
        channel3.setLightColor(R.color.colorPrimary);
        channel3.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel3);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public NotificationManager getManager(){
        if (this.mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public NotificationCompat.Builder getChannel_1_Notification(String title, String message){
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.mipmap.ic_launcher);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public NotificationCompat.Builder getChannel_2_Notification(String title, String message){
        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        intent.addCategory(Intent. CATEGORY_LAUNCHER ) ;
        intent.setAction(Intent. ACTION_MAIN ) ;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_2_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public NotificationCompat.Builder getChannel_3_Notification(String title, String message, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_3_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
