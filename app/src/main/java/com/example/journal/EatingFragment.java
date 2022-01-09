package com.example.journal;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class EatingFragment extends Fragment {
    String eatingName = "Завтрак";
    int bgColor, textColor;

    TextView eatingText;
    LinearLayout dishContainer;
    Button addDish;


    public static EatingFragment newInstance(String eatingName, int bgColor, int textColor) {
        EatingFragment eatingFragment = new EatingFragment();

        Bundle args = new Bundle();

        args.putString("EatingName", eatingName);
        args.putInt("BgColor", bgColor);
        args.putInt("TextColor", textColor);

        eatingFragment.setArguments(args);
        return eatingFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        eatingName = getArguments().getString("EatingName", eatingName);
        bgColor = getArguments().getInt("BgColor", bgColor);
        textColor = getArguments().getInt("TextColor", textColor);
    }

    @Override
    // Переопределяем метод onCreateView
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.eating_fragment,
                container,false);

        eatingText = (TextView) view.findViewById(R.id.eatingName);
        eatingText.setText(eatingName);
        eatingText.setTextColor(textColor);
        eatingText.setBackgroundColor(bgColor);

        View line = (View) view.findViewById(R.id.downBorder);
        line.setBackgroundColor(bgColor);

        addDish = (Button) view.findViewById(R.id.addNewDish);
        dishContainer = (LinearLayout) view.findViewById(R.id.dishContainer);

        addDish.setTextColor(textColor);

        return view;
    }
}
