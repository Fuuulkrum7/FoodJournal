package com.example.journal.ui.search;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.journal.DatabaseInterface;
import com.example.journal.EatingFragmentsController;
import com.example.journal.R;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchFragment extends Fragment {
    CalendarView calendarView;
    DatabaseInterface database;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Создаем интерфейс для бд
        database = new DatabaseInterface(getContext());
        EatingFragmentsController controller = new EatingFragmentsController(
                getResources().getStringArray(R.array.EatingTimes), this
        );

        // Получаем все и вся
        controller.breakfastContainer = (LinearLayout) view.findViewById(R.id.breakfastContainer);
        controller.lunchContainer = (LinearLayout) view.findViewById(R.id.lunchContainer);
        controller.dinnerContainer = (LinearLayout) view.findViewById(R.id.dinnerContainer);
        controller.otherContainer = (LinearLayout) view.findViewById(R.id.otherContainer);

        controller.addBreakfast = (Button) view.findViewById(R.id.addBreakfast);
        controller.addLunch = (Button) view.findViewById(R.id.addLunch);
        controller.addDinner = (Button) view.findViewById(R.id.addDinner);
        controller.addOther = (Button) view.findViewById(R.id.addOther);

        // Ставим прослушку на кнопки
        controller.addBreakfast.setOnClickListener(controller);
        controller.addLunch.setOnClickListener(controller);
        controller.addDinner.setOnClickListener(controller);
        controller.addOther.setOnClickListener(controller);

        Date currentDate = new Date();
        String date = (new SimpleDateFormat("dd.MM.yyyy")).format(currentDate);

        database.getDishes(date, controller);

        calendarView = (CalendarView) view.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                LocalDate date1 = new LocalDate();
                LocalDate date2 = new LocalDate(year, month + 1, day);
                int date = Days.daysBetween(date1, date2).getDays();

                if (date  * -1 > 3 || date > 0)
                    controller.hideButtons();
                else
                    controller.showButtons();

                controller.clearAllData();
                database.getDishes(String.format("%d.%d%d.%d",
                        day, (month + 1) / 10,(month + 1) % 10, year), controller);
            }
        });

        return view;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}