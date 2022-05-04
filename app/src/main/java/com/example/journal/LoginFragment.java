package com.example.journal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements View.OnClickListener {
    Button enter;
    Button registration;
    EditText password;
    EditText login;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        login = (EditText) view.findViewById(R.id.login);
        password = (EditText) view.findViewById(R.id.password);

        enter = (Button) view.findViewById(R.id.enter);
        registration = (Button) view.findViewById(R.id.registration);

        enter.setOnClickListener(this);
        registration.setOnClickListener(this);
        return view;
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
                    DatabaseInterface databaseInterface = new DatabaseInterface(getContext());
                    databaseInterface.getUser(
                            l,
                            p,
                            this);
                }
                else {
                    Toast.makeText(getContext(), "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.registration:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity)getActivity();
        if (activity != null) {
            activity.showUpButton();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity activity = (MainActivity)getActivity();
        if (activity != null) {
            activity.hideUpButton();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity)getActivity()).onBackPressed();
                onDestroy();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onUserFound(boolean equals){
        if (!equals){
            // Небольшой костыль. Так как вызываю метод из другого потока, пришлось делать так
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.getContext(), "No user found", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {

        }
    }

}
