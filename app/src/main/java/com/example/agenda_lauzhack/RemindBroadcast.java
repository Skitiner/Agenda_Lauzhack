package com.example.agenda_lauzhack;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class RemindBroadcast extends BroadcastReceiver {
    private String CHANNEL_ID = "";
    private int notificationId = 0;
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent start = new Intent(context, AgendaActivity.class);
        start.putExtra("POPUP", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, start, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.organisemois)
                .setContentTitle("NEW ACTIVITY !")
                .setContentText("Go Check in the application")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Go Check in the application"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_background, "Start", pendingIntent)
                .addAction(R.drawable.rabbit_background, "In 15 min", pendingIntent)
                .addAction(R.drawable.rabbit_background, "Cancel", pendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

}
