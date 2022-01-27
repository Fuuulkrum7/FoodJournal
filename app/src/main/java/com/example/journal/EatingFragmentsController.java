package com.example.journal;

import android.os.Bundle;
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
    private MainActivity mainActivity;

    public EatingFragmentsController(String[] eating, MainActivity mainActivity){
        this.eating = eating;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View view) {
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

    private DishFragment createFragment(int layout, int eating, Bundle args){
        DishFragment dishFragment = createFragment(layout, eating);
        dishFragment.setArguments(args);

        return dishFragment;
    }

    private DishFragment createFragment(int layout, int eating){
        FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
        DishFragment fragment = DishFragment.newInstance(mainActivity.database, eating);
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

            DishFragment fragment = createFragment(layout.getId(), eat, args);

            //fragment.setNewData(map.get("dish"), map.get("mass"));
        }
    }
}
