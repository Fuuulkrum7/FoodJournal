package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.journal.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // Некоторые статические перемнные, в частности контекст и разное время птитания
    private static Context context;

    // Интерфейс для работы с бд
    public DatabaseInterface database;

    // Индекс выбранного времени пищи (дабы сразу при выборе его сохранять в таком виде)
    // Как-никак, в бд значение этой переменной является числом для удобства сортировки
    // И для экономии памяти
    int eating_index = 0;

    // Необходимо для работы с панелью навигации.
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Алгоритм ниже нужен для того, чтобы задать управление навигацией.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Видимо, данная переменная создаётся для взаимодействия с панелью навигации.
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_friends, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Сохраняем контекст
        MainActivity.context = getApplicationContext();

        String[] eating_values = getResources().getStringArray(R.array.EatingTimes);

        // Создаем интерфейс для бд
        database = new DatabaseInterface(getContext());
        EatingFragmentsController controller = new EatingFragmentsController(eating_values);

        // Получаем все и вся
        controller.breakfastContainer = (LinearLayout) findViewById(R.id.breakfastContainer);
        controller.lunchContainer = (LinearLayout) findViewById(R.id.lunchContainer);
        controller.dinnerContainer = (LinearLayout) findViewById(R.id.dinnerContainer);
        controller.otherContainer = (LinearLayout) findViewById(R.id.otherContainer);

        controller.addBreakfast = (Button) findViewById(R.id.addBreakfast);
        controller.addLunch = (Button) findViewById(R.id.addLunch);
        controller.addDinner = (Button) findViewById(R.id.addDinner);
        controller.addOther = (Button) findViewById(R.id.addOther);

        Date date1 = new Date();
        String date = (new SimpleDateFormat("dd.MM.yyyy")).format(date1);

        database.GetDishes(date, controller);

        // Ставим прослушку на кнопки
        controller.addBreakfast.setOnClickListener(controller);
        controller.addLunch.setOnClickListener(controller);
        controller.addDinner.setOnClickListener(controller);
        controller.addOther.setOnClickListener(controller);
    }
    /*
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // Если нажата кнопка добавить блюдо
            case R.id.AddDish:
                // Получаем выбранное блюдо и массу в качестве строки
                String current_dish = dish.getText().toString(),
                       s_mass = mass.getText().toString();

                // Как известно, масса является числом положительным. Дробным быть может, но зачем?
                // Нам лишнее место в бд занимать нет смысла. Поэтому проверяем, является ли масса
                // натуральным числом. И ввели ли что-то в поле "блюдо"
                if (isNatural(s_mass) && current_dish.length() > 0){
                    // Добавляем блюдо в бд
                    database.AddDish(current_dish, Integer.parseInt(s_mass), eating_index);

                    // Сбрасываем все значения
                    dish.setText("");
                    mass.setText("");
                    eating.setSelection(0);
                }
                // Тут, думаю, и так все понятно
                else if (current_dish.length() == 0){
                    Toast toast = Toast.makeText(MainActivity.getContext(),
                            "Введите название блюда", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(MainActivity.getContext(),
                            "Масса должна быть натуральным числом!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
        }
    }*/

    // Метод для получения контекста
    public static Context getContext() {
        return MainActivity.context;
    }

    // Является ли число натуральным, без пояснений
    private boolean isNatural(String s){
        try{
            return Integer.parseInt(s) > 0;
        }
        catch (NumberFormatException e){
            return false;
        }
    }
}