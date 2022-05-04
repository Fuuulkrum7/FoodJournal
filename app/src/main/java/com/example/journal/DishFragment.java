package com.example.journal;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DishFragment extends Fragment implements View.OnClickListener {
    Button addDish;
    EditText mass, dish, calories;
    DatabaseInterface database;
    String date;
    boolean visibility = false;
    int id;
    boolean enable = true;

    View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(MainActivity.TAG, enable + "");
                handler.postDelayed(longPressed, ViewConfiguration.getLongPressTimeout());
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP){
                handler.removeCallbacks(longPressed);
            }
            return false;
        }
    };


    final Handler handler = new Handler(Looper.getMainLooper());
    Runnable longPressed = new Runnable() {
        @Override
        public void run() {
            updateOptionsMenu();
        }
    };

    // Индекс выбранного времени пищи (дабы сразу при выборе его сохранять в таком виде)
    // Как-никак, в бд значение этой переменной является числом для удобства сортировки
    // И для экономии памяти
    int eating_index = 0;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dish_fragment,
                container, false);

        view.setOnTouchListener(listener);

        database = new DatabaseInterface(MainActivity.getContext());
        eating_index = getArguments().getInt("eating");
        date = getArguments().getString("date");

        mass = (EditText) view.findViewById(R.id.mass);
        dish = (EditText) view.findViewById(R.id.dishName);
        calories = (EditText) view.findViewById(R.id.calories);

        dish.setOnTouchListener(listener);

        addDish = (Button) view.findViewById(R.id.addDish);
        addDish.setOnClickListener(this);

        if (getArguments().containsKey("dish")){
            setNewData(
                    getArguments().getString("dish"),
                    getArguments().getString("mass"),
                    getArguments().getString("calories")
            );
            id = getArguments().getInt("id");
            disable();
        }

        Log.d("TEST", (addDish == null) + "");

        return view;
    }


    public static DishFragment newInstance(int eating_index, String date) {
        DishFragment dishFragment = new DishFragment();

        Bundle args = new Bundle();
        args.putInt("eating", eating_index);
        args.putString("date", date);
        dishFragment.setArguments(args);

        return dishFragment;
    }

    public void updateOptionsMenu() {
        visibility = !visibility;
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // Если нажата кнопка добавить блюдо
            case R.id.addDish:
                // Получаем выбранное блюдо и массу в качестве строки
                String current_dish = dish.getText().toString(),
                        s_mass = mass.getText().toString(),
                        s_calories = calories.getText().toString();

                // Как известно, масса является числом положительным. Дробным быть может, но зачем?
                // Нам лишнее место в бд занимать нет смысла. Поэтому проверяем, является ли масса
                // натуральным числом. И ввели ли что-то в поле "блюдо"
                if (isNatural(s_mass) && isNatural(s_calories) && current_dish.length() > 0){
                    // Добавляем блюдо в бд
                    // Здесь будут данные для добавления в бд
                    ContentValues values = new ContentValues();

                    // Здесь получаем текущее время
                    Date date1 = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    String time = formatter.format(date1);

                    values.put(DatabaseInfo.COLUMN_DISH, current_dish);
                    values.put(DatabaseInfo.COLUMN_MASS, Integer.parseInt(s_mass));
                    values.put(DatabaseInfo.COLUMN_CALORIES, Integer.parseInt(s_calories));
                    values.put(DatabaseInfo.COLUMN_EATING, eating_index);
                    values.put(DatabaseInfo.COLUMN_DATE, date);
                    values.put(DatabaseInfo.COLUMN_TIME, time);
                    Log.d(MainActivity.TAG, time);

                    Log.d(MainActivity.TAG, date);

                    database.addData(values, DatabaseInfo.JOURNAL_TABLE);

                    disable();
                }
                // Тут, думаю, и так все понятно
                else if (current_dish.length() == 0){
                    Toast toast = Toast.makeText(MainActivity.getContext(),
                            "Введите название блюда", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(MainActivity.getContext(),
                            "Масса и калории должны быть натуральными числами!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void disable(){
        enable = !enable;
        dish.setFocusable(enable);
        dish.setClickable(true);
        mass.setFocusable(enable);
        mass.setClickable(true);
        calories.setFocusable(enable);
        calories.setClickable(true);
        if (!enable){
            addDish.setVisibility(View.GONE);
            calories.setInputType(InputType.TYPE_NULL);
            mass.setInputType(InputType.TYPE_NULL);
            dish.setInputType(InputType.TYPE_NULL);
        }
        else {
            addDish.setVisibility(View.VISIBLE);
            calories.setInputType(InputType.TYPE_CLASS_NUMBER);
            mass.setInputType(InputType.TYPE_CLASS_NUMBER);
            dish.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        }
    }

    private void setNewData(String dishName, String massData, String caloriesData){
        dish.setText(dishName);
        mass.setText(massData);
        calories.setText(caloriesData);
    }
}
