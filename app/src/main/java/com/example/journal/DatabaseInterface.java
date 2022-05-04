package com.example.journal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseInterface extends SQLiteOpenHelper {
    // Данные по бд
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "LocalJournal.db";

    // Инициализация, ничего интересного
    public DatabaseInterface(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // При создании бд
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseInfo.SQL_CREATE_JOURNAL);
        db.execSQL(DatabaseInfo.SQL_CREATE_USER);
    }

    // При апдейте бд, а вдруг что-то сильно поменялось. В будущем доработать
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseInfo.SQL_DELETE_JOURNAL);
        db.execSQL(DatabaseInfo.SQL_DELETE_USER);
        onCreate(db);
    }

    // Добавляем блюдо в бд
    public void addData(ContentValues values, String table){
        // Получаем бд для записи данных
        SQLiteDatabase db = this.getWritableDatabase();

        AddData adder = new AddData(values, db, table);
        adder.start();
    }

    // Соответсвтвенно, получем блюда
    public void getDishes(String date, EatingFragmentsController controller){
        SQLiteDatabase db = this.getReadableDatabase();
        GetDish getDish = new GetDish(date, db, controller);
        getDish.start();
    }

    public void getUser(String login, String password, LoginFragment fragment){
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            password = getSSH512(password);
            GetUser getUser = new GetUser(db, login, password, fragment);
            getUser.start();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.getContext(), "Can't use db", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getSSH512(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] digest = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}


class AddData extends Thread{
    private ContentValues values;
    SQLiteDatabase db;
    String table;

    public AddData(ContentValues values, SQLiteDatabase db, String table){
        this.values = values;
        this.db = db;
        this.table = table;
    }

    @Override
    public void run(){
        // Добавляем в бд
        try {
            db.insert(table, null, values);
        }
        // Если что-то пошло не так, то вот
        catch (Exception e){
            Log.d("TEST", e.toString());
            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось добавить данные", Toast.LENGTH_SHORT);

            toast.show();
        }
    }
}


class GetUser extends Thread {
    SQLiteDatabase db;
    String login;
    String password;
    LoginFragment loginFragment;

    public GetUser(SQLiteDatabase db, String login, String password, LoginFragment loginFragment){
        this.db = db;
        this.login = login;
        this.password = password;
        this.loginFragment = loginFragment;
    }

    @Override
    public void run() {
        // Зададим условие для выборки - список столбцов
        String[] projection = {
                DatabaseInfo.COLUMN_PASSWORD
        };

        // Формируем строку-выборку
        String selection = DatabaseInfo.COLUMN_LOGIN + " = '" + login + "'";


        Cursor cursor;
        try {
            // Делаем запрос
            cursor = db.query(
                    DatabaseInfo.USER_TABLE,
                    projection,
                    selection,
                    null,
                    null,
                    null,
                    null
            );
        }
        catch (Exception e){
            // Если что-то пошло не так
            Log.d("TEST", e.toString());

            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось получить данные", Toast.LENGTH_SHORT);

            toast.show();
            return;
        }

        // Узнаем индекс каждого столбца
        int passwordColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_PASSWORD);
        cursor.moveToNext();
        String new_password = "";
        if (cursor.getCount() != 0){
            new_password = cursor.getString(passwordColumnIndex);
        }

        try {
            loginFragment.onUserFound(DatabaseInterface.getSSH512(new_password).equals(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            loginFragment.onUserFound(false);
        }
    }
}


class GetDish extends Thread{
    String date;
    SQLiteDatabase db;
    EatingFragmentsController controller;

    public GetDish(String date, SQLiteDatabase db, EatingFragmentsController controller){
        this.date = date;
        this.db = db;
        this.controller = controller;
    }
    @Override
    public void run(){

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                DatabaseInfo.COLUMN_DISH,
                DatabaseInfo.COLUMN_MASS,
                DatabaseInfo.COLUMN_CALORIES,
                DatabaseInfo.COLUMN_EATING,
                DatabaseInfo.COLUMN_TIME,
                DatabaseInfo.COLUMN_ID
        };



        // Формируем строку-выборку
        String selection = DatabaseInfo.COLUMN_DATE + " = '" + date + "'";

        // Порядок сортировки. Сгачала сортируем по дате, а потом уже по еде
        String sortOrder =
                DatabaseInfo.COLUMN_DATE + ", " + DatabaseInfo.COLUMN_EATING + " ASC";

        Cursor cursor;
        try {
            // Делаем запрос
            cursor = db.query(
                    DatabaseInfo.JOURNAL_TABLE,
                    projection,
                    selection,
                    null,
                    null,
                    null,
                    sortOrder
            );
        }
        catch (Exception e){
            // Если что-то пошло не так
            Log.d("TEST", e.toString());

            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось получить данные", Toast.LENGTH_SHORT);

            toast.show();

            return;
        }

        // Узнаем индекс каждого столбца
        int dishColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_DISH);
        int massColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_MASS);
        int caloriesColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_CALORIES);
        int eatingColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_EATING);
        int timeColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_TIME);
        int idColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_ID);

        // Словарь, оно же хеш-таблица для наших данных
        Map<Integer, List<Map<String, String>>> result = new HashMap<Integer, List<Map<String, String>>>();

        int _eating = 0;
        List<Map<String, String>> dishResult = new ArrayList<Map<String, String>>();

        // Пока в запросе ещё что-то есть
        while(cursor.moveToNext()) {
            // Получаем данные из запроса
            String currentDish = cursor.getString(dishColumnIndex);
            String currentMass = Integer.toString(cursor.getInt(massColumnIndex));
            String calories = Integer.toString(cursor.getInt(caloriesColumnIndex));
            int currentEating = cursor.getInt(eatingColumnIndex);
            String currentTime = cursor.getString(timeColumnIndex);
            String id = Integer.toString(cursor.getInt(idColumnIndex));


            if (_eating != currentEating){
                result.put(_eating, dishResult);
                _eating = currentEating;
                // Эта зараза при использовании clear чистит все и в словаре
                dishResult = new ArrayList<Map<String, String>>();
            }
            Map<String, String> dishData = new HashMap<String, String>();

            dishData.put("dish", currentDish);
            dishData.put("mass", currentMass);
            dishData.put("calories", calories);
            dishData.put("time", currentTime);
            dishData.put("id", id);

            dishResult.add(dishData);
        }
        result.put(_eating, dishResult);
        // Закрываем курсор
        cursor.close();

        controller.SetData(result);
    }
}

/*
    Выглядит так

    {
    "ТЕКУЩЕЕ ВРЕМЯ ПРИЕМА ЕДЫ": [
            {
                "id": "0",
                "dish": "борщ",
                "mass": "300",
                "time": "20:10:23"
            },
            {
                "id": "1",
                "dish": "Чай",
                "mass": "350",
                "time": "01.12.59"
            }
        ]
    }
*/
