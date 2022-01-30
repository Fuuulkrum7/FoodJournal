package com.example.journal.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.journal.DatabaseInterface;
import com.example.journal.EatingFragmentsController;
import com.example.journal.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {
    // Интерфейс для работы с бд
    public DatabaseInterface database;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        Date date1 = new Date();
        String date = (new SimpleDateFormat("dd.MM.yyyy")).format(date1);

        database.getDishes(date, controller);

        return view;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}