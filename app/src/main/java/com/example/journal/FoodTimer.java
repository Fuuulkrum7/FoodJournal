package com.example.journal;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.journal.ui.settings.SettingsFragment;

public class FoodTimer extends Fragment {
    private static SettingsFragment settingsFragment;
    public TextView time;
    public Switch need_timer;
    int number;
    SharedPreferences settings;
    int currentHour = 7;
    int currentMinute = 0;

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time.setText(hourOfDay / 10 + "" + hourOfDay % 10 + ":" + minute / 10 + "" + minute % 10);
            if (currentHour != hourOfDay || currentMinute != minute)
                need_timer.setChecked(true);
            currentHour = hourOfDay;
            currentMinute = minute;

            saveData();

            settingsFragment.startNotificationService();
        }
    };

    private void saveData(){
        String name = SettingsFragment.APP_PREFERENCES_TIMES[number];
        // Сохраняем время
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name + "checked", need_timer.isChecked());
        editor.putInt(name + "hour", currentHour);
        editor.putInt(name + "minute", currentMinute);
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_timer, parent, false);

        settings = getActivity().getSharedPreferences(SettingsFragment.APP_PREFERENCES, Context.MODE_PRIVATE);

        time = (TextView) view.findViewById(R.id.textTimerBreakfast);
        need_timer = (Switch) view.findViewById(R.id.switchForBreakfast);

        number = getArguments().getInt("number");
        String name = SettingsFragment.APP_PREFERENCES_TIMES[number];

        currentHour = settings.getInt(name + "hour", 8 + number * 6);
        currentMinute = settings.getInt(name + "minute", 0);
        need_timer.setChecked(settings.getBoolean(name + "checked", true));

        time.setText(currentHour / 10 + "" + currentHour % 10 + ":" + currentMinute / 10 + "" + currentMinute % 10);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        timeSetListener, currentHour, currentMinute, true);
                timePickerDialog.show();
            }
        });

        need_timer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveData();
            }
        });

        return view;
    }

    public static FoodTimer newInstance(int num, SettingsFragment sFragment){
        FoodTimer foodTimer = new FoodTimer();
        settingsFragment = sFragment;
        Bundle args = new Bundle();
        args.putInt("number", num);

        foodTimer.setArguments(args);
        return foodTimer;
    }
}
