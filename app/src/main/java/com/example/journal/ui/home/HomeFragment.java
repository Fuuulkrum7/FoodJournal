package com.example.journal.ui.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.journal.DatabaseInterface;
import com.example.journal.EatingFragmentsController;
import com.example.journal.MainActivity;
import com.example.journal.R;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    // Интерфейс для работы с бд
    public DatabaseInterface database;
    ConstraintLayout layout;
    EatingFragmentsController controller;
    CalendarView calendar;
    Calendar date;

    CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
            date = Calendar.getInstance();
            date.set(year, month, day);
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
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);

        // Создаем интерфейс для бд
        database = new DatabaseInterface(getContext());
        controller = new EatingFragmentsController(
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

        Date date1 = new Date();
        String date = (new SimpleDateFormat("dd.MM.yyyy")).format(date1);

        database.getDishes(date, controller);

        layout = (ConstraintLayout) view.findViewById(R.id.homePage);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_page_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_calendar:  {
                if (calendar != null){
                    ((ScrollView) getActivity().findViewById(R.id.scrollView)).animate()
                            .translationY(0);

                    calendar.setAlpha(0);
                    calendar.animate()
                        .translationY(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layout.removeView(calendar);
                                calendar = null;
                            }
                        });

                    return true;
                }

                ConstraintSet set = new ConstraintSet();

                calendar = new CalendarView(MainActivity.getContext());
                calendar.setOnDateChangeListener(onDateChangeListener);
                calendar.setId(View.generateViewId());
                if (date != null)
                    calendar.setDate(date.getTimeInMillis());

                layout.addView(calendar, 0);

                ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView);
                set.clear(scrollView.getId(), ConstraintSet.TOP);

                set.connect(scrollView.getId(), ConstraintSet.TOP, calendar.getId(), ConstraintSet.BOTTOM);
                set.connect(calendar.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);

                // TODO исправить баг со съезжанием scrollView
                scrollView.animate()
                        .translationY(calendar.getHeight() + scrollView.getHeight() + 100);
                calendar.setAlpha(0f);
                calendar.animate()
                        .translationY(calendar.getHeight())
                        .alpha(1.0f)
                        .setListener(null);

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}