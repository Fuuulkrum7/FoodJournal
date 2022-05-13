package com.example.journal;

import android.content.Context;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteDatabaseInterface {
    public static final String URL = "http://f0653156.xsph.ru";

    public void addUser(String login, String password, String name) {
        Log.d(MainActivity.TAG, "0");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JournalAPI journalAPI = retrofit.create(JournalAPI.class);

        User user = new User(login, password, name, "");
        Call<User> call = journalAPI.addUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()){
                    Log.d(MainActivity.TAG, "Server error: " + response.code() + "\n" + response.toString());
                    return;
                }

                User responseAPI = response.body();

                Context context = MainActivity.getContext();
                if (context == null){
                    context = LoginFragment.getContext();
                }

                DatabaseInterface databaseInterface = new DatabaseInterface(context);
                Log.d(MainActivity.TAG, responseAPI.getId());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(MainActivity.TAG, "error");
                t.printStackTrace();
            }
        });
    }
}
