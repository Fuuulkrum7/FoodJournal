package com.example.journal;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.journal.ui.friends.FriendsFragment;
import com.example.journal.ui.home.HomeFragment;
import com.example.journal.ui.search.SearchFragment;
import com.example.journal.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.journeyapps.barcodescanner.ScanIntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityResultCallback<ScanIntentResult> {
    // Некоторые статические перемнные, в частности контекст и разное время птитания
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    public static final String TAG = "JournalMain";

    private final BottomNavigationView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnItemSelectedListener() {

        @SuppressLint("NonConstantResourceId")
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
        ft.addToBackStack("Main");
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new HomeFragment());

        SharedPreferences settings = getSharedPreferences(SettingsFragment.APP_PREFERENCES, Context.MODE_PRIVATE);

        // DELETE
        RemoteDatabaseInterface remoteDatabaseInterface = new RemoteDatabaseInterface();
        remoteDatabaseInterface.addUser("123", "123", "1223");

        if (settings.getBoolean(SettingsFragment.APP_PREFERENCES_REMINDER, false)){
            Log.d(TAG, "running service...");
            ArrayList<Integer> times = new ArrayList<>();

            Intent serviceIntent = new Intent(this, JournalNotificationService.class);
            int startTime = 8;
            for (String time: SettingsFragment.APP_PREFERENCES_TIMES
                 ) {
                if (settings.getBoolean(time + "checked", false))
                    times.add(
                            settings.getInt(time + "hour", startTime) * 60 +
                                    settings.getInt(time + "minute", 0)
                    );

                startTime += 6;
            }
            serviceIntent.putExtra("times", times);
            stopService(new Intent(this, JournalNotificationService.class));
            startService(serviceIntent);
        }

        // Сохраняем контекст
        MainActivity.context = getApplicationContext();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onActivityResult(ScanIntentResult result) {
        Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_LONG).show();
    }

    // Метод для получения контекста
    public static Context getContext() {
        return MainActivity.context;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack("Main", 0);
            fragmentManager.beginTransaction().commit();

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}