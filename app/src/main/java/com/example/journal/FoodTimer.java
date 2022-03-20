package com.example.journal;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

public class FoodTimer extends Fragment {
    public TextView time;
    public Switch need_timer;
    int currentHour = 7;
    int currentMinute = 0;
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time.setText(hourOfDay / 10 + "" + hourOfDay % 10 + ":" + minute / 10 + "" + minute % 10);
            if (currentHour != hourOfDay || currentMinute != minute)
                need_timer.setChecked(true);
            currentHour = hourOfDay;
            currentMinute = minute;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_timer, parent, false);

        time = (TextView) view.findViewById(R.id.textTimerBreakfast);
        need_timer = (Switch) view.findViewById(R.id.switchForBreakfast);

        currentHour = getArguments().getInt("Hour");
        currentMinute = getArguments().getInt("Minute");
        time.setText(currentHour / 10 + "" + currentHour % 10 + ":" + currentMinute / 10 + "" + currentMinute % 10);
        need_timer.setChecked(getArguments().getBoolean("isChecked"));

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        timeSetListener, currentHour, currentMinute, true);
                timePickerDialog.show();
            }
        });

        return view;
    }

    public static FoodTimer newInstance(int hour, int minute, boolean isChecked){
        FoodTimer foodTimer = new FoodTimer();

        Bundle args = new Bundle();
        args.putInt("Hour", hour);
        args.putInt("Minute", minute);
        args.putBoolean("isChecked", isChecked);

        foodTimer.setArguments(args);
        return foodTimer;
    }
}
