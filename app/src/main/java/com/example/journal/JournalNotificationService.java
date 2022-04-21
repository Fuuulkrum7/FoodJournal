package com.example.journal;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;

public class JournalNotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public JournalNotificationService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(MainActivity.TAG, "onCreate Intent");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        assert intent != null;
        Bundle args = intent.getExtras();

        if (args != null){
            ArrayList<Integer> times = args.getIntegerArrayList("times");

            if (!times.isEmpty()){
                Collections.sort(times);

                for (int time: times){
                    AlarmManager alarmManager =  (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    Intent intent1 = new Intent(this, AlarmReceiver.class);
                    @SuppressLint("UnspecifiedImmutableFlag")
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, time / 60);
                    calendar.set(Calendar.MINUTE, time % 60);

                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                    );
                }
            }
        }
        return START_STICKY;
    }
}
