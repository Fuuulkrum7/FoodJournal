package com.example.journal;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;
import java.util.Map;

public class EatingFragmentsController implements View.OnClickListener {
    // А здесь лежат нужные нам элементы интерфейса
    public Button addBreakfast, addLunch, addDinner, addOther;
    public LinearLayout breakfastContainer, lunchContainer, dinnerContainer, otherContainer;
    public String[] eating;
    private DatabaseInterface database;
    private MainActivity mainActivity;

    public EatingFragmentsController(String[] eating, DatabaseInterface database, MainActivity mainActivity){
        this.eating = eating;
        this.database = database;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addBreakfast:
                createFragment(breakfastContainer, 0);
                break;
            case R.id.addLunch:
                createFragment(lunchContainer, 1);
                break;
            case R.id.addDinner:
                createFragment(dinnerContainer, 2);
                break;
            case R.id.addOther:
                createFragment(otherContainer, 3);
                break;
        }
    }

    private DishFragment createFragment(LinearLayout layout, int eating){
        FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
        DishFragment fragment = DishFragment.newInstance(database, true, eating);
        ft.add(layout.getId(), fragment);
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
        Log.d("TEST", "here");

        for (Map<String, String> map: dishes){
            DishFragment fragment = createFragment(layout, eat);

            fragment.setNewData(map.get("dish"), map.get("mass"));
        }
    }
}
