package com.example.journal;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RegistrationFragment extends Fragment implements View.OnClickListener {
    Button registrate;
    EditText username;
    EditText password;
    EditText login;

    String name;
    String passwordText;
    String loginText;

    boolean done = false;

    int countL = 0;
    int countP = 0;
    int countU = 0;

    private void updateButton(){
       done = (countL >= 4 && countP >= 8 && countU >= 3);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration_fragment, container, false);

        username = (EditText) view.findViewById(R.id.username_reg);
        password = (EditText) view.findViewById(R.id.password_reg);
        login = (EditText) view.findViewById(R.id.login_reg);

        login.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countL = count;
                updateButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countU = count;
                updateButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countP = start - before + 1;
                updateButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        registrate = (Button) view.findViewById(R.id.registration_reg);
        registrate.setOnClickListener(this);

        return view;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registration_reg){
            if (!done){
                Toast.makeText(getContext(), "Длина логина должна быть больше 4, пароля - 8", Toast.LENGTH_SHORT).show();
                return;
            }
            name = username.getText().toString();
            passwordText = password.getText().toString();
            loginText = login.getText().toString();

            DatabaseInterface databaseInterface = new DatabaseInterface(getContext());

            ContentValues values = new ContentValues();
            values.put(DatabaseInfo.COLUMN_NAME, name);
            values.put(DatabaseInfo.COLUMN_PASSWORD, passwordText);
            values.put(DatabaseInfo.COLUMN_LOGIN, loginText);

            AddUser addUser = databaseInterface.addUser(values);
            Sync sync = new Sync(addUser);
            sync.start();
        }
    }

    class Sync extends Thread {
        AddUser addUser;

        public Sync(AddUser addUser){
            this.addUser = addUser;
        }

        @Override
        public void run(){
            try {
                addUser.join();
                if (addUser.success){
                    RemoteDatabaseInterface remoteDatabaseInterface = new RemoteDatabaseInterface();
                    RemoteDatabaseInterface.AddRemoteUser thread = remoteDatabaseInterface.addUser(loginText, passwordText, name);

                    thread.join();

                    int id = thread.getUserId();
                    DatabaseInterface databaseInterface = new DatabaseInterface(getContext());

                    if (id > 0){
                        databaseInterface.addUserId(id);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().onBackPressed();
                            }
                        });
                    }
                    else {
                        databaseInterface.deleteUsers();

                        String text = "Не удалось добавить пользователя";
                        if (id == -2)
                            text = "Пользовтель уже существует";

                        String finalText = text;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), finalText, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
