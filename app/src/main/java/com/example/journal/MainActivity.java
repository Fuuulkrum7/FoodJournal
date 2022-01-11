package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Некоторые статические перемнные, в частности контекст и разное время птитания
    private static Context context;
    private static String[] eating_values;

    // Интерфейс для работы с бд
    public DatabaseInterface database;

    // А здесь лежат нужные нам элементы интерфейса
    Spinner eating, sortKey;
    Button addItem, getItem;
    EditText dish, mass;
    TextView result;

    // Индекс выбранного времени пищи (дабы сразу при выборе его сохранять в таком виде)
    // Как-никак, в бд значение этой переменной является числом для удобства сортировки
    // И для экономии памяти
    int eating_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Сохраняем контекст
        MainActivity.context = getApplicationContext();

        // Создаем интерфейс для бд
        database = new DatabaseInterface(getContext());

        // Получаем все и вся
        eating = (Spinner) findViewById(R.id.Eating);
        sortKey = (Spinner) findViewById(R.id.SortKey);

        addItem = (Button) findViewById(R.id.AddDish);
        getItem = (Button) findViewById(R.id.GetItems);

        dish = (EditText) findViewById(R.id.Dish);
        mass = (EditText) findViewById(R.id.Mass);

        result = (TextView) findViewById(R.id.Result);

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

            // Если нажата кнопка выбрать блюда
            case R.id.GetItems:
                // Получаем ключ для выборки
                String key = sortKey.getSelectedItem().toString();

                // Получаем позицию ключа
                ArrayAdapter adapter = (ArrayAdapter) sortKey.getAdapter();
                int position = adapter.getPosition(key);

                Map dishes = database.GetDishes(position);
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