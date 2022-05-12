package com.example.journal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.journal.ui.settings.SettingsFragment;

public class LoginFragment extends AppCompatActivity implements View.OnClickListener {
    Button enter;
    Button registration;
    EditText password;
    EditText login;
    boolean entered = false;
    String name = SettingsFragment.APP_PREFERENCES;
    String key = SettingsFragment.APP_PREFERENCES_SYNCHRONIZATION;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);

        enter = (Button) findViewById(R.id.enter);
        registration = (Button) findViewById(R.id.registration_reg);

        enter.setOnClickListener(this);
        registration.setOnClickListener(this);

        // Сохраняем контекст
        LoginFragment.context = getApplicationContext();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enter:
                String l = login.getText().toString();
                String p = password.getText().toString();
                Log.d(MainActivity.TAG, p + l.length());
                if (l.length() > 0 && p.length() > 0){
                    DatabaseInterface databaseInterface = new DatabaseInterface(this);
                    databaseInterface.checkUser(
                            l,
                            p,
                            this);
                }
                else {
                    Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.registration_reg:
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistrationFragment rf = new RegistrationFragment();
                fragmentTransaction.replace(android.R.id.content, rf)
                        .attach(rf)
                        .addToBackStack(null)
                        .commit();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onUserFound(boolean equals){
        if (!equals){
            // Небольшой костыль. Так как вызываю метод из другого потока, пришлось делать так
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            entered = true;
            this.finish();
        }
    }

    @Override
    public void onDestroy(){
        SharedPreferences settings = getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(
                key,
                entered
        );
        editor.apply();

        Log.d(MainActivity.TAG, entered + "");

        super.onDestroy();
    }

    // Метод для получения контекста
    public static Context getContext() {
        return LoginFragment.context;
    }
}
