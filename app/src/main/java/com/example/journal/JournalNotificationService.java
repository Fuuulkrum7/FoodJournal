package com.example.journal;

import static java.lang.Math.abs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JournalNotificationService extends Service {

    public JournalNotificationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Bundle args = intent.getExtras();

        if (args != null){
            ArrayList times = args.getIntegerArrayList("times");
            if (!times.isEmpty()){
                NotifyUser notifyUser = new NotifyUser(times, this);
                notifyUser.start();
            }
        }

        return Service.START_STICKY;
    }
}


class NotifyUser extends Thread{
    private List<Integer[]> times;
    private Service service;

    public NotifyUser(ArrayList<Integer[]> time, Service service) {
        times = time;
        Collections.sort(times, new Comparator<Integer[]>() {
            @Override
            public int compare(Integer[] integers, Integer[] t1) {
                return (integers[0] * 60 + integers[1] < t1[0] * 60 + t1[1] ? -1 :
                        (integers[0] * 60 + integers[1] == t1[0] * 60 + t1[1] ? 0 : 1));
            }
        });
        this.service = service;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run(){

        while (true){
            DateTime dt = new DateTime();
            int minute = dt.getMinuteOfHour();
            int hours = dt.getHourOfDay();
            long delay = 0;
            for (int i = 0; i < times.size(); i++) {
                if (hours <= times.get(i)[0] && minute < times.get(i)[1]){
                    delay = 60000L * ((times.get(i)[0] - hours) * 60 + abs(times.get(i)[1] - minute));
                    Log.d(MainActivity.TAG, "" + (times.get(i)[0] - hours) * 60);
                    break;
                }
                else  if (i + 1 == times.size()){
                    Log.d(MainActivity.TAG, "i" + (times.get(i)[0] + 23 - hours) * 60);
                    delay = 60000L * ((times.get(i)[0] + 23 - hours) * 60 + (60 - times.get(i)[1]) + minute);
                }
            }

            try {
                Log.d(MainActivity.TAG, "" + delay / 60000);
                Thread.sleep(delay);
            }
            catch (Exception e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
                break;
            }
            notifyHim();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notifyHim() {
        int id = 1;
        String channelID = service.getString(R.string.channel_id);
        NotificationChannel notificationChannel = new NotificationChannel(
                channelID,
                service.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.setDescription(service.getString(R.string.channel_description));
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.WHITE);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        Intent notificationIntent = new Intent(service, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(service,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, "Дневник питания")
                .setSmallIcon(R.drawable.notebook)
                .setContentTitle("Напоминание")
                .setContentText("Пора заполнить дневник")
                .setChannelId(channelID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(service);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(id, builder.build());
    }
}
