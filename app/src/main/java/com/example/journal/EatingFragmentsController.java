package com.example.journal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;
import java.util.Map;

public class EatingFragmentsController implements View.OnClickListener {
    // А здесь лежат нужные нам элементы интерфейса
    public Button addBreakfast, addLunch, addDinner, addOther;
    public LinearLayout breakfastContainer, lunchContainer, dinnerContainer, otherContainer;
    public String[] eating;
    public String date;
    Fragment activity;

    public EatingFragmentsController(String[] eating, Fragment activity){
        this.eating = eating;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        // Добавляем контейнер под блюдо
        switch (view.getId()){
            case R.id.addBreakfast:
                createFragment(breakfastContainer.getId(), 0);
                break;
            case R.id.addLunch:
                createFragment(lunchContainer.getId(), 1);
                break;
            case R.id.addDinner:
                createFragment(dinnerContainer.getId(), 2);
                break;
            case R.id.addOther:
                createFragment(otherContainer.getId(), 3);
                break;
        }
    }

    // Чистим данные в завтраке и тд
    public boolean clearAllData(){
        try {
            breakfastContainer.removeAllViews();
            lunchContainer.removeAllViews();
            dinnerContainer.removeAllViews();
            otherContainer.removeAllViews();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public void setData(View view){
        // Получаем все и вся
        breakfastContainer = (LinearLayout) view.findViewById(R.id.breakfastContainer);
        lunchContainer = (LinearLayout) view.findViewById(R.id.lunchContainer);
        dinnerContainer = (LinearLayout) view.findViewById(R.id.dinnerContainer);
        otherContainer = (LinearLayout) view.findViewById(R.id.otherContainer);

        addBreakfast = (Button) view.findViewById(R.id.addBreakfast);
        addLunch = (Button) view.findViewById(R.id.addLunch);
        addDinner = (Button) view.findViewById(R.id.addDinner);
        addOther = (Button) view.findViewById(R.id.addOther);

        // Ставим прослушку на кнопки
        addBreakfast.setOnClickListener(this);
        addLunch.setOnClickListener(this);
        addDinner.setOnClickListener(this);
        addOther.setOnClickListener(this);
    }

    private void changeButtonsCondition(int i){
        addBreakfast.setVisibility(i);
        addDinner.setVisibility(i);
        addLunch.setVisibility(i);
        addOther.setVisibility(i);
    }

    // прячем
    public void hideButtons(){
        changeButtonsCondition(View.GONE);
    }

    // и показываем кнопки
    public void showButtons(){
        changeButtonsCondition(View.VISIBLE);
    }

    // Контейнер для блюда с аргументами для фрагмента
    private DishFragment createFragment(int layout, int eating, Bundle args){
        DishFragment dishFragment = createFragment(layout, eating);
        dishFragment.setArguments(args);

        return dishFragment;
    }

    // Дефолтный способ создания контейнера для блюда, без аргументов
    private DishFragment createFragment(int layout, int eating){
        DishFragment fragment = DishFragment.newInstance(eating, date);
        FragmentTransaction ft = activity.getChildFragmentManager().beginTransaction();
        ft.add(layout, fragment);
        ft.commit();

        return fragment;
    }

    public void SetData(Map<Integer, List<Map<String, String>>> data){
        LinearLayout[] linearLayouts = {
                breakfastContainer, lunchContainer, dinnerContainer, otherContainer
        };

        for (int i = 0; i < linearLayouts.length; i++){
            parseMap(linearLayouts[i], data.get(i), i);
        }
    }

    private void parseMap(LinearLayout layout, List<Map<String, String>> dishes, int eat){
        if (dishes == null)
            return;

        for (Map<String, String> map: dishes){
            Bundle args = new Bundle();
            args.putString("dish", map.get("dish"));
            args.putString("mass", map.get("mass"));
            args.putString("calories", map.get("calories"));
            args.putInt("id", Integer.parseInt(map.get("id")));
            createFragment(layout.getId(), eat, args);
        }
    }
}
