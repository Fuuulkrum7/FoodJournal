package com.example.journal.ui.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

    // Прослушка на календарь на выбор даты
    CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
            // Сохраняем выбранную дату в переменную, чтобы после уничтожения календаря пользователь
            // при повторном открытии календаря увидел выбранную ранее дату (а не текущую)
            date = Calendar.getInstance();
            date.set(year, month, day);
            LocalDate date1 = new LocalDate();
            LocalDate date2 = new LocalDate(year, month + 1, day);

            // Узнаем, сколько дней прошло с выбранного дня
            int date = Days.daysBetween(date1, date2).getDays();

            // если прошло более трех дней или выбран один из следующих, прячем кнопки
            if (date  * -1 > 3 || date > 0)
                controller.hideButtons();
            else
                controller.showButtons();

            @SuppressLint("DefaultLocale")
            String current_date = String.format("%d%d.%d%d.%d",
                    day / 10, day % 10,
                    (month + 1) / 10,(month + 1) % 10,
                    year
            );

            // Выводим новые данные по выбранной дате на экран
            controller.clearAllData();
            database.getDishes(current_date, controller);
            controller.date = current_date;
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

        Date date1 = new Date();
        @SuppressLint("SimpleDateFormat")
        String date = (new SimpleDateFormat("yyyy-dd-MM")).format(date1);

        // Получаем все и вся
        controller.setData(view);
        // Запоминаем текущую дату
        controller.date = date;

        database.getDishes(date, controller);

        layout = (ConstraintLayout) view.findViewById(R.id.homePage);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_page_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_calendar:  {
                // Если календарь уже создан
                if (calendar != null){
                    // начинаем двигать scrollView назад, попутно вернув ему нормальные размеры
                    ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView);
                    scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    scrollView.animate()
                            .translationY(0);

                    // Прячем календарь, ставя альфа-канал на 0 и двигая его вверх
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

                // Штука для добавления новых элементов в контсреинт
                ConstraintSet set = new ConstraintSet();

                // Создаем календарь, попутно выставляя нужные парметры и привязывая прослушку на вабор даты
                calendar = new CalendarView(MainActivity.getContext());
                calendar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                calendar.setOnDateChangeListener(onDateChangeListener);
                calendar.setId(View.generateViewId());

                // Если переменная, сохраняющая дату, уже существует
                // (т.е. когда мы один раз выбрали дату с помощью календаря, мы ее так запоминаем)
                if (date != null)
                    calendar.setDate(date.getTimeInMillis());

                // добавляем календарь
                layout.addView(calendar, 0);

                // Отвязываем прокрутку от верхней части страницы
                ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView);
                set.clear(scrollView.getId(), ConstraintSet.TOP);

                // Привязываем кандарь и прокрутку
                set.connect(scrollView.getId(), ConstraintSet.TOP, calendar.getId(), ConstraintSet.BOTTOM);
                set.connect(calendar.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);

                // Это прослушка, которая будет вызвана после того, как календарь создастся
                // и мы наконец-то сможем получить его высоту
                calendar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Чтобы не было последующих вызовов
                        // (они происходят при каждом изменении календаря)
                        // мы отвязываем прослушку
                        calendar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                         // Двигаем прокуртку
                        scrollView.animate()
                                .translationY(calendar.getHeight());
                    }
                });

                // Заставляем календарь красиво появиться
                calendar.setAlpha(0f);
                calendar.animate()
                        .translationY(calendar.getHeight())
                        .alpha(1.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                // Уменьшаем размеры прокрутки, так как оно само не понимает, что часть его уехало
                                scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, scrollView.getHeight() - calendar.getHeight()));
                            }
                        });

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