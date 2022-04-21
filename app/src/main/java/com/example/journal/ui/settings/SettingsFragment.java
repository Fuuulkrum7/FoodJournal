package com.example.journal.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.journal.FoodTimer;
import com.example.journal.JournalNotificationService;
import com.example.journal.MainActivity;
import com.example.journal.R;

import java.util.ArrayList;

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
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();

        for (int i = 0; i < APP_PREFERENCES_TIMES.length; i++){
            // Создаем напоминалки (визуальные) и добавляем их
            times[i] = FoodTimer.newInstance(i, this);
            ft.add(R.id.Timers, times[i]);
        }

        ft.commit();

        // Если в настройках сохранено предыдущая информация по уведомлениям
        if (settings.contains(APP_PREFERENCES_REMINDER)){
            need_to_remind.setChecked(settings.getBoolean(APP_PREFERENCES_REMINDER, true));
            if (need_to_remind.isChecked())
                ((LinearLayout) view.findViewById(R.id.Timers)).setVisibility(View.VISIBLE);
        }

        // Прослушка на переключатель
        need_to_remind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Если надо напоминалку
                if (isChecked)  {
                    // Отображаем всю инфу с будильниками недоделанными
                    startNotificationService();
                    ((LinearLayout) getView().findViewById(R.id.Timers)).setVisibility(View.VISIBLE);
                }
                else {
                    // Прячем
                    getActivity().stopService(new Intent(getActivity(), JournalNotificationService.class));
                    ((LinearLayout) getView().findViewById(R.id.Timers)).setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroyView() {
        SharedPreferences.Editor editor = settings.edit();
        boolean notify = need_to_remind.isChecked();
        editor.putBoolean(APP_PREFERENCES_REMINDER, notify);
        editor.apply();

        if (notify)
            startNotificationService();
        super.onDestroyView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startNotificationService(){
        // Сохраняем все и вся
        ArrayList<Integer> allTimes = new ArrayList<>();
        for (int i = 0; i < APP_PREFERENCES_TIMES.length; i++){
            FoodTimer fragment = times[i];
            boolean notifyUser = fragment.need_timer.isChecked();

            String[] timer = fragment.time.getText().toString().split(":");
            int hour = Integer.parseInt(timer[0]);
            int minute = Integer.parseInt(timer[1]);


            if (notifyUser){
                allTimes.add(hour * 60 + minute);
            }
        }

        Log.d(MainActivity.TAG, "running service...");
        Intent serviceIntent = new Intent(getContext(), JournalNotificationService.class);
        serviceIntent.putExtra("times", allTimes);

        getActivity().stopService(new Intent(getActivity(), JournalNotificationService.class));
        getActivity().startService(serviceIntent);

    }
}