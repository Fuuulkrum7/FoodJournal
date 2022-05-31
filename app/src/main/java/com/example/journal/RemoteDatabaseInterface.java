package com.example.journal;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteDatabaseInterface {
    public static final String LINK = "http://f0653156.xsph.ru";
    public static final String USERNAME_FIELD = "username";
    public static final String LOGIN_FIELD = "login";
    public static final String PASSWORD_FIELD = "password";

    public void addUser(String login, String password, String name) {
        Log.d(MainActivity.TAG, "0");
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    URL requestUrl = new URL(LINK + "/add_user.php");
                    HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Accept-Charset", "UTF-8");
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    try
                    {
                        connection.connect();
                        DataOutputStream stream = new DataOutputStream(connection.getOutputStream());
                        String json = String.format(
                                "{ \"%s\" : \"%s\", \"%s\" : \"%s\", \"%s\" : \"%s\"}",
                                USERNAME_FIELD, name, LOGIN_FIELD, login, PASSWORD_FIELD, password);
                        Log.d(MainActivity.TAG, json);
                        stream.writeBytes(json);
                        stream.flush();
                        stream.close();
                        InputStream in = new BufferedInputStream(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        int userID = 0;
                        try
                        {
                            userID = Integer.parseInt(result.toString());
                        }
                        catch (NumberFormatException ex)
                        {
                            ex.printStackTrace();
                            Log.e(MainActivity.TAG, "Bad server response.");
                        }
                        //Дальше, я полагаю, надо куда-то этот номер сохранить.
                        Log.d("test", "result from server: " + result.toString());
                    } finally {
                        connection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(MainActivity.TAG, "Failure while creating a url or working with connection.");
                }
            }
        };
        thread.start();
    }
}
