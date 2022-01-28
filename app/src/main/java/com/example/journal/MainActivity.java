package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.journal.databinding.ActivityMainBinding;
import com.example.journal.ui.friends.FriendsFragment;
import com.example.journal.ui.home.HomeFragment;
import com.example.journal.ui.search.SearchFragment;
import com.example.journal.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // Некоторые статические перемнные, в частности контекст и разное время птитания
    private static Context context;

    // Интерфейс для работы с бд
    public DatabaseInterface database;

    private BottomNavigationView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(new HomeFragment());
                    return true;
                case R.id.navigation_search:
                    loadFragment(new SearchFragment());
                    return true;
                case R.id.navigation_friends:
                    loadFragment(new FriendsFragment());
                    return true;
                case R.id.navigation_settings:
                    loadFragment(new SettingsFragment());
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, fragment);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new HomeFragment());

        // Сохраняем контекст
        MainActivity.context = getApplicationContext();
        String[] eating_values = getResources().getStringArray(R.array.EatingTimes);

        // Создаем интерфейс для бд
        database = new DatabaseInterface(getContext());
        EatingFragmentsController controller = new EatingFragmentsController(
                eating_values, this
        );

        // Получаем все и вся
        controller.breakfastContainer = (LinearLayout) findViewById(R.id.breakfastContainer);
        controller.lunchContainer = (LinearLayout) findViewById(R.id.lunchContainer);
        controller.dinnerContainer = (LinearLayout) findViewById(R.id.dinnerContainer);
        controller.otherContainer = (LinearLayout) findViewById(R.id.otherContainer);

        controller.addBreakfast = (Button) findViewById(R.id.addBreakfast);
        controller.addLunch = (Button) findViewById(R.id.addLunch);
        controller.addDinner = (Button) findViewById(R.id.addDinner);
        controller.addOther = (Button) findViewById(R.id.addOther);

        Date date1 = new Date();
        String date = (new SimpleDateFormat("dd.MM.yyyy")).format(date1);

        database.getDishes(date, controller);

        // Ставим прослушку на кнопки
        controller.addBreakfast.setOnClickListener(controller);
        controller.addLunch.setOnClickListener(controller);
        controller.addDinner.setOnClickListener(controller);
        controller.addOther.setOnClickListener(controller);
    }

    // Метод для получения контекста
    public static Context getContext() {
        return MainActivity.context;
    }
}