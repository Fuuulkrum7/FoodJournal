package com.example.journal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// Класс-фрагмент для календаря
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private int id;
    private DatabaseInterface database;

    // id, кто создал календарь, дабы понимать, куда что девать
    public DatePickerFragment(int id){
        this.id = id;
    }

    public  DatePickerFragment(int id, DatabaseInterface database){
        this(id);
        this.database = database;
    }

    // Создаем календарь
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // Получаем данные о текущем дне
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Создаем окошко выбора даты
        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                this,
                year, month, day
        );

        // Возвращаем его
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Если окно нужно было для получения даты для выборки в бд
        if (id == R.id.SortKey){
            month += 1;
            // Ставим дату в бд
            database.setDate(String.format("%d%d.%d%d.%d", day / 10, day % 10,
                    month / 10, month % 10,
                    year));
        }
    }
}
