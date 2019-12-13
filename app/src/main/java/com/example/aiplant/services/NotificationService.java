package com.example.aiplant.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.aiplant.R;
import com.example.aiplant.home.HomeActivity;

import java.util.concurrent.atomic.AtomicInteger;


public class NotificationService extends Service {

    private final String TAG = "NotificationService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void createNotification(Context context, String titleNotification, String textNotification, AtomicInteger requestCode) {
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode.get(), intent, 0);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, "channel");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.eye_plant_log_with_name)
                    .setContentTitle(titleNotification)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(textNotification))
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentInfo("Info")
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setContentIntent(pendingIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = null;

                String NOTIFICATION_CHANNEL_ID = "1001";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(notificationChannel);

                b.setChannelId(NOTIFICATION_CHANNEL_ID);

            }
            assert notificationManager != null;
            notificationManager.notify(requestCode.getAndIncrement(), b.build());

        } else {

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.eye_plant_log_with_name)
                    .setContentTitle(titleNotification)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(textNotification))
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentInfo("Info")
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setContentIntent(pendingIntent);
            assert notificationManager != null;
            notificationManager.notify(requestCode.getAndIncrement(), b.build());
        }

    }
}

