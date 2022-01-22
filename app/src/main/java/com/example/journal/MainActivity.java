package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journal.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Некоторые статические перемнные, в частности контекст и разное время птитания
    private static Context context;
    private static String[] eating_values;

    // Интерфейс для работы с бд
    public DatabaseInterface database;

    // А здесь лежат нужные нам элементы интерфейса
    Button addBreakfast, addLunch, addDinner, addOther;
    LinearLayout breakfastContainer, lunchContainer, dinnerContainer, otherContainer;

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

        // Создаем интерфейс для бд
        database = new DatabaseInterface(getContext());

        // Получаем все и вся
        breakfastContainer = (LinearLayout) findViewById(R.id.breakfastContainer);
        lunchContainer = (LinearLayout) findViewById(R.id.lunchContainer);
        dinnerContainer = (LinearLayout) findViewById(R.id.dinnerContainer);
        otherContainer = (LinearLayout) findViewById(R.id.otherContainer);

        dish = (EditText) findViewById(R.id.Dish);
        mass = (EditText) findViewById(R.id.Mass);

        result = (TextView) findViewById(R.id.Result);

        Date date1 = new Date();
        String date = (new SimpleDateFormat("dd.MM.yyyy")).format(date1);

        database.GetDishes(date);

        // Ставим прослушку на кнопки
        addItem.setOnClickListener(this);
        getItem.setOnClickListener(this);

        // Прослушка на выбор элемента из списка вариантов еды
        eating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                // Вот тут и сохраням наше числовое значение
                eating_index = selectedItemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Ещё прослушка, ту на вибор даты
        sortKey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Вот здесь хз, как лучше. С одной стороны, более оптимально использовать число
                // С другой, использование текста на выбранном элементе было бы более универсально
                switch (position){
                    case 1:
                        // Создаем фрагмент с календарем и отображаем его
                        DialogFragment DateFragment = new DatePickerFragment(R.id.SortKey, database);
                        DateFragment.show(getSupportFragmentManager(), "datePicker");

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Сохраняем "приемы" пищи в список.
        // Ну вот не получается получить в другом классе ресурсы из String.xml
        eating_values = getResources().getStringArray(R.array.EatingTimes);
        TypedArray bgColors = getResources().obtainTypedArray(R.array.EatingBgColors);
        TypedArray textColors = getResources().obtainTypedArray(R.array.EatingTextColors);
        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragMan.beginTransaction();

        for (int i = 0; i < eating_values.length; i++) {
            EatingFragment eatingFragment = EatingFragment.newInstance(
                    eating_values[i],
                    bgColors.getColor(i, 0),
                    textColors.getColor(i, 0)
            );

            fragmentTransaction.add(R.id.eatingLayout, eatingFragment);
        }

        fragmentTransaction.commit();
    }

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
    }

    // Метод для получения контекста
    public static Context getContext() {
        return MainActivity.context;
    }

    // Метод получения списка времени приема пищи
    public static String[] getEating_values(){
        return eating_values;
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