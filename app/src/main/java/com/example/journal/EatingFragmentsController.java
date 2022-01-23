package com.example.journal;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class EatingFragmentsController implements View.OnClickListener {
    // А здесь лежат нужные нам элементы интерфейса
    public Button addBreakfast, addLunch, addDinner, addOther;
    public LinearLayout breakfastContainer, lunchContainer, dinnerContainer, otherContainer;
    public String[] eating;

    public EatingFragmentsController(String[] eating){
        this.eating = eating;
    }

    @Override
    public void onClick(View view) {
        LinearLayout layout;
        switch (view.getId()){
            case R.id.addBreakfast:
                layout = breakfastContainer;
            case R.id.addLunch:
                layout = lunchContainer;
            case R.id.addDinner:
                layout = dinnerContainer;
            case R.id.addOther:
                layout = otherContainer;
            default:
                // Здесь создается в layout фрагмент для добавляения нового блюда
        }
    }

    public void SetData(Map data){

    }

    private void parseMap(LinearLayout layout, List<Map<String, String>> dishes){
        for (Map<String, String> map: dishes){
            // TODO здесь надо будет добавлять нормальные фрагменты
            TextView text = new TextView(MainActivity.getContext());
            text.setText(map.get("dish") + "\n" + map.get("mass") + "\n" + map.get("time") + "\n");
            layout.addView(text);
        }
    }
}
