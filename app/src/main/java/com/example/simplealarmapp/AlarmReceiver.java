package com.example.simplealarmapp;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.simplealarmapp.custom_alarm")){

            String channelId = intent.getStringExtra("channelId");
            String title = intent.getStringExtra("title");
            String[] days = intent.getStringArrayExtra("days");
            List<String> listOfDays = Arrays.asList(days.clone());

            if (listOfDays.size() > 0){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDate localDate = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
                    String day = formatter.format(localDate);

                    boolean contains = listOfDays.contains(day);
                    if (contains){
                        showNotification(context, title, channelId);
                    }
                }
            }else {
                showNotification(context, title, channelId);
            }
        }
    }

    private void showNotification(Context context, String title, String channelId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText("Alarm & Events")
                .setSmallIcon(IconCompat.createWithResource(context, R.drawable.ic_time))
                .build();
        notificationManager.notify(1, notification);
    }
}
