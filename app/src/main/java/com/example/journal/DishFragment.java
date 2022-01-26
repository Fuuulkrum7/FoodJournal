package com.example.journal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class DishFragment extends Fragment implements View.OnClickListener {
    Button addDish;
    EditText mass, dish;
    DatabaseInterface database;

    // Индекс выбранного времени пищи (дабы сразу при выборе его сохранять в таком виде)
    // Как-никак, в бд значение этой переменной является числом для удобства сортировки
    // И для экономии памяти
    int eating_index = 0;
    boolean showButton = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dish_fragment,
                container, false);

        showButton = getArguments().getBoolean("showButton");
        eating_index = getArguments().getInt("eating");

        return view;
    }


    @Override
    public void onStart(){
        super.onStart();

        mass = (EditText) getActivity().findViewById(R.id.Mass);
        dish = (EditText) getActivity().findViewById(R.id.DishName);

        addDish = (Button) getActivity().findViewById(R.id.AddDish);
        Log.d("TEST", addDish.getText().toString());
        addDish.setOnClickListener(this);

        if (showButton)
            addDish.setVisibility(View.VISIBLE);
    }


    public static DishFragment newInstance(
            DatabaseInterface database, boolean showButton, int eating_index) {
        DishFragment dishFragment = new DishFragment();
        dishFragment.database = database;

        Bundle args = new Bundle();
        args.putBoolean("showButton", showButton);
        args.putInt("eating", eating_index);
        dishFragment.setArguments(args);

        return dishFragment;
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
                    database.addDish(current_dish, Integer.parseInt(s_mass), eating_index);

                    //
                    mass.setEnabled(false);
                    dish.setEnabled(false);
                    addDish.setVisibility(View.GONE);
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

    public void setNewData(String dishName, String massData){
        dish.setText(dishName);
        mass.setText(massData);
    }
}
