package com.example.journal;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteDatabaseInterface {
    //Константы с данными для отправки. Link содержит ссылку на сайт.
    public static final String LINK = "http://f0653156.xsph.ru";
    public static final String USERNAME_FIELD = "username";
    public static final String LOGIN_FIELD = "login";
    public static final String PASSWORD_FIELD = "password";

    //Регистрирует пользователя в приложении
    public AddRemoteUser addUser(String login, String password, String name) {
        Log.d(MainActivity.TAG, "register");
        //Создаём пользователя
        User user = new User(login, password, name);
        //Регистрируем его
        AddRemoteUser thread = new AddRemoteUser(user);
        thread.start();

        return thread;
    }

    class AddRemoteUser extends Thread{
        int userID = -1;//ID пользователя, который мы возвращаем после регистрации.
        User user;

        public AddRemoteUser(User user) {
            this.user = user;
        }

        @Override
        public void run() {
            try {
                //Создаём POST-запрос, через который отправим данные.
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
                    String json = user.getQuery();//Получаем данные пользователя для запроса.
                    Log.d(MainActivity.TAG, json);
                    stream.writeBytes(json);
                    stream.flush();
                    stream.close();
                    //Получаем ответ на запрос.
                    Log.d(MainActivity.TAG, connection.getResponseCode() + "");
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    //ПОлучаем ID пользователя из ответа.
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

        public int getUserId() {
            return userID;
        }
    };
    //Добавляет данные о блюде на сервер.
    public void addDish(int id, String name, int mass, int calories, int eatingIndex, String date, String time)
    {
        Log.d(MainActivity.TAG, "add dish");
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {//Аналогично, составляем запрос.
                    URL requestUrl = new URL(LINK + "/add_dish.php");
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
                        int userId = 0;

                        DatabaseInterface databaseInterface = new DatabaseInterface(MainActivity.getContext());
                        GetUserId getUserId = databaseInterface.getId();
                        getUserId.join();

                        userId = getUserId.getUId();
                        //Формируем содержимое запроса.
                        String json = String.format(
                                "%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
                                "local_id", id, "dish", name, "mass", mass, "calories", calories,
                                "eating", eatingIndex, "date", date, "time", time, "user_id", userId);
                        Log.d(MainActivity.TAG, json);
                        stream.writeBytes(json);
                        stream.flush();
                        stream.close();
                        //Если код нормальный, то блюдо успешно добавлено.
                        if (connection.getResponseCode() == 400) Log.e(MainActivity.TAG, "Problems with connection data.");
                        else Log.d(MainActivity.TAG, "Dish has been added successfully.");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
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
