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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.journal.FoodTimer;
import com.example.journal.R;

public class SettingsFragment extends Fragment {
    // это будет именем файла настроек
    public static final String APP_PREFERENCES = "journal";
    public static final String APP_PREFERENCES_REMINDER = "Reminder";
    // Надо ли включать напоминалку
    Switch need_to_remind;
    // Получаем доступ к сохраненным данным в настройках
    SharedPreferences settings;
    FoodTimer breakfast;
    FoodTimer lunch;
    FoodTimer dinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Данные про настройки, собсна
        settings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        // Получаем переключатель
        need_to_remind = (Switch) view.findViewById(R.id.Reminder);

        // Создаем напоминалки (визуальные) и добавляем их
        breakfast = FoodTimer.newInstance(7, 0, false);
        lunch = FoodTimer.newInstance(14, 0, false);
        dinner = FoodTimer.newInstance(18, 0, false);
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.add(R.id.Timers, breakfast);
        ft.add(R.id.Timers, lunch);
        ft.add(R.id.Timers, dinner);
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
        editor.apply();
        super.onDestroyView();
    }
}