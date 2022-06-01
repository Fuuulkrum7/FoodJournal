package com.example.journal;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
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
    boolean change = false;
    int id = -1;
    boolean enable = true;

    // прослушка на долгое нажатия для создания всплывающего меню
    View.OnLongClickListener listener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            // Отображаем менюшку, если мы не редактируем данные о блюде прямо сейчас
            if (!change && !enable)
                showPopup(v);
            return false;
        }
    };

    // Обработка на выбор элемента из меню
    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                // Если нажали на кнопку удалить
                case R.id.show_dustbin:
                    deleteFood();
                    return true;
                case R.id.show_pencil:
                    Log.d(MainActivity.TAG, id + "");
                    if (id == -1)
                        return true;
                    change = true;
                    disable();
                    return true;
                default:
                    return false;
            }
        }
    };

    protected void setId(int id){
        this.id = id;
    }

    protected void deleteFood(){
        if (id == -1)
            return;
        database.deleteData(id, DatabaseInfo.JOURNAL_TABLE, DatabaseInfo.COLUMN_ID);
        // Это должно удалить текущий фрагмент.
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

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

        view.setOnLongClickListener(listener);

        database = new DatabaseInterface(MainActivity.getContext());
        eating_index = getArguments().getInt("eating");
        date = getArguments().getString("date");

        mass = (EditText) view.findViewById(R.id.mass);
        dish = (EditText) view.findViewById(R.id.dishName);
        calories = (EditText) view.findViewById(R.id.calories);

        dish.setOnLongClickListener(listener);
        mass.setOnLongClickListener(listener);
        calories.setOnLongClickListener(listener);

        addDish = (Button) view.findViewById(R.id.addDish);
        addDish.setOnClickListener(this);

        dish.setClickable(true);
        mass.setClickable(true);
        calories.setClickable(true);

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


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(onMenuItemClickListener);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_menu, popup.getMenu());
        popup.show();
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


    private ContentValues getValues(){
        // Получаем выбранное блюдо и массу в качестве строки
        String current_dish = dish.getText().toString(),
                s_mass = mass.getText().toString(),
                s_calories = calories.getText().toString();

        // Здесь получаем текущее время
        Date date1 = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String time = formatter.format(date1);

        ContentValues values = new ContentValues();
        values.put(DatabaseInfo.COLUMN_DISH, current_dish);
        values.put(DatabaseInfo.COLUMN_MASS, Integer.parseInt(s_mass));
        values.put(DatabaseInfo.COLUMN_CALORIES, Integer.parseInt(s_calories));
        values.put(DatabaseInfo.COLUMN_TIME, time);

        return values;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        // Если нажата кнопка добавить блюдо
        if (v.getId() == R.id.addDish) {
            // Получаем выбранное блюдо и массу в качестве строки
            String current_dish = dish.getText().toString();

            // Как известно, масса является числом положительным. Дробным быть может, но зачем?
            // Нам лишнее место в бд занимать нет смысла. Поэтому проверяем, является ли масса
            // натуральным числом. И ввели ли что-то в поле "блюдо"
            if (isNatural(mass.getText().toString())
                    && isNatural(calories.getText().toString())
                    && current_dish.length() > 0) {
                // Добавляем блюдо в бд
                // Здесь будут данные для добавления в бд
                ContentValues values = getValues();
                if (change){
                    database.updateData(id, values);
                }
                else {
                    values.put(DatabaseInfo.COLUMN_EATING, eating_index);
                    values.put(DatabaseInfo.COLUMN_DATE, date);

                    database.addDish(values, this);


                }

                change = false;
                disable();
            }
            // Тут, думаю, и так все понятно
            else if (current_dish.length() == 0) {
                Toast toast = Toast.makeText(MainActivity.getContext(),
                        "Введите название блюда", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(MainActivity.getContext(),
                        "Масса и калории должны быть натуральными числами!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void disable(){
        enable = !enable;

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
