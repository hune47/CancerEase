package com.graduation.CancerEaseProj.Utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.graduation.CancerEaseProj.Activities.WelcomeActivity;
import com.graduation.CancerEaseProj.R;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Random;

import static com.graduation.CancerEaseProj.Utilities.Constants.DOCTORS_COLLECTION;

////////////////////////////////////////////////////////////////////////////////////////////////////
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "CancerEaseLog: Noti_Serv";
    private SharedPref sharedPref;
    private SharedPreferences preferences;
    private String NOTIFICATION_CHANNEL_ID = "CancerEase_channel_001";
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sharedPref = new SharedPref(this);
        sharedPref.setDeviceToken(token);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Intent intent = new Intent(this, WelcomeActivity.class);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);

      /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
      */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon( R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        sharedPref = new SharedPref(this);
        if (sharedPref.getAccountType().equals(DOCTORS_COLLECTION))
        notificationManager.notify(notificationID, notificationBuilder.build());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Doctor notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
}