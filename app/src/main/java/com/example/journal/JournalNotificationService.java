package com.example.journal;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;

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
    }
    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        assert intent != null;
        Bundle args = intent.getExtras();

        if (args != null){
            ArrayList<Integer> times = args.getIntegerArrayList("times");

            if (times != null && !times.isEmpty()){
                Collections.sort(times);

                for (int i = 0; i < times.size(); i++){
                    int time = times.get(i);
                    AlarmManager alarmManager =  (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, time / 60);
                    calendar.set(Calendar.MINUTE, time % 60);
                    calendar.set(Calendar.SECOND, 0);
                    if (calendar.before(Calendar.getInstance())) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    Intent intent1 = new Intent(this, AlarmReceiver.class);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this,
                            i,
                            intent1,
                            PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setWindow(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                        );
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    }
                }
            }
        }
        return START_STICKY;
    }
}
