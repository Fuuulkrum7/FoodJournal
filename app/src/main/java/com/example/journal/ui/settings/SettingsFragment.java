package com.example.journal.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.journal.FoodTimer;
import com.example.journal.R;

public class SettingsFragment extends Fragment {
    // это будет именем файла настроек
    public static final String APP_PREFERENCES = "journal";
    public static final String APP_PREFERENCES_REMINDER = "Reminder";
    public static final String[] APP_PREFERENCES_TIMES = new String[]{"breakfast_", "lunch_", "dinner_"};

    // Надо ли включать напоминалку
    Switch need_to_remind;
    // Получаем доступ к сохраненным данным в настройках
    SharedPreferences settings;
    FoodTimer[] times = new FoodTimer[3];

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Данные про настройки, собсна
        settings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        // Получаем переключатель
        need_to_remind = (Switch) view.findViewById(R.id.Reminder);

        for (int i = 0; i < APP_PREFERENCES_TIMES.length; i++){
            // Создаем напоминалки (визуальные) и добавляем их
            int hour = settings.getInt(APP_PREFERENCES_TIMES[i] + "hour", 8 + i * 6);
            int minute = settings.getInt(APP_PREFERENCES_TIMES[i] + "minute", 0);
            boolean isChecked = settings.getBoolean(APP_PREFERENCES_TIMES[i] + "checked", true);

            times[i] = FoodTimer.newInstance(hour, minute, isChecked);
        }

        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.add(R.id.Timers, times[0]);
        ft.add(R.id.Timers, times[1]);
        ft.add(R.id.Timers, times[2]);
        ft.commit();

        // Если в настройках сохранено предыдущая информация по уведомлениям
        if (settings.contains(APP_PREFERENCES_REMINDER)){
            Log.d("TEST", "here");
            need_to_remind.setChecked(settings.getBoolean(APP_PREFERENCES_REMINDER, true));
            if (need_to_remind.isChecked())
                ((LinearLayout) view.findViewById(R.id.Timers)).setVisibility(View.VISIBLE);
        }

        // Прослушка на переключатель
        need_to_remind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Если надо напоминалку
                if (isChecked)  {
                    // Отображаем всю инфу с будильниками недоделанными
                    ((LinearLayout) getView().findViewById(R.id.Timers)).setVisibility(View.VISIBLE);
                }
                else {
                    // Прячем
                    ((LinearLayout) getView().findViewById(R.id.Timers)).setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        // Сохраняем все и вся
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_REMINDER, need_to_remind.isChecked());
        int id = 101;

        for (int i = 0; i < APP_PREFERENCES_TIMES.length; i++){
            String time = APP_PREFERENCES_TIMES[i];
            FoodTimer fragment = times[i];

            editor.putBoolean(time + "checked", fragment.need_timer.isChecked());

            if (fragment.need_timer.isChecked()){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "Дневник питания")
                        .setSmallIcon(R.drawable.notebook)
                        .setContentTitle("Напоминание")
                        .setContentText("Пора заполнить дневник")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(getActivity());
                notificationManager.notify(id, builder.build());
                id += 1;
            }

            String[] timer = fragment.time.getText().toString().split(":");
            int hour = Integer.parseInt(timer[0]);
            int minute = Integer.parseInt(timer[1]);

            editor.putInt(time + "hour", hour);
            editor.putInt(time + "minute", minute);
        }
        editor.apply();
        super.onDestroyView();
    }
}