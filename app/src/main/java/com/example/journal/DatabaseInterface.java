package com.example.journal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatabaseInterface extends SQLiteOpenHelper {
    // Данные по бд
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LocalJournal.db";

    // Инициализация, ничего интересного
    public DatabaseInterface(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // При создании бд
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseInfo.SQL_CREATE_ENTRIES);
    }

    // При апдейте бд, а вдруг что-то сильно поменялось. В будущем доработать
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseInfo.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // Добавляем блюдо в бд
    public void addDish(String dish, int mass, int eating){
        // Здесь получаем текщую дату и время
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date1 = new Date();

        String _date = formatter.format(date1);

        formatter = new SimpleDateFormat("HH:mm");
        String time = formatter.format(date1);

        // Получаем бд для записи данных
        SQLiteDatabase db = this.getWritableDatabase();

        // Здесь будут данные для добавления в бд
        ContentValues values = new ContentValues();

        values.put(DatabaseInfo.COLUMN_DISH, dish);
        values.put(DatabaseInfo.COLUMN_MASS, mass);
        values.put(DatabaseInfo.COLUMN_EATING, eating);
        values.put(DatabaseInfo.COLUMN_DATE, _date);
        values.put(DatabaseInfo.COLUMN_TIME, time);

        AddDish adder = new AddDish(values, db);
        adder.start();
    }

    // Соответсвтвенно, получем блюда
    public void getDishes(String date, EatingFragmentsController controller){
        SQLiteDatabase db = this.getReadableDatabase();
        GetDish getDish = new GetDish(date, db, controller);
        getDish.start();
    }
}


class AddDish extends Thread{
    private ContentValues values;
    SQLiteDatabase db;

    public AddDish(ContentValues values, SQLiteDatabase db){
        this.values = values;
        this.db = db;
    }

    @Override
    public void run(){
        // Добавляем в бд
        try {
            long newRowId = db.insert(DatabaseInfo.TABLE_NAME, null, values);
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
                DatabaseInfo.COLUMN_EATING,
                DatabaseInfo.COLUMN_TIME
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
                    DatabaseInfo.TABLE_NAME,
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
        int eatingColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_EATING);
        int timeColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_TIME);

        // Словарь, оно же хеш-таблица для наших данных
        Map result = new HashMap<Integer, List>();

        int _eating = 0;
        List<Map> dishResult = new ArrayList<Map>();

        // Пока в запросе ещё что-то есть
        while(cursor.moveToNext()) {
            // Получаем данные из запроса
            String currentDish = cursor.getString(dishColumnIndex);
            String currentMass = Integer.toString(cursor.getInt(massColumnIndex));
            int currentEating = cursor.getInt(eatingColumnIndex);
            String currentTime = cursor.getString(timeColumnIndex);

            if (_eating != currentEating){
                result.put(_eating, dishResult);
                _eating = currentEating;
                // Эта зараза при использовании clear чистит все и в словаре
                dishResult = new ArrayList<Map>();
            }
            Log.d("TEST", currentEating + " " + currentDish);
            Map dishData = new HashMap<String, String>();

            dishData.put("dish", currentDish);
            dishData.put("mass", currentMass);
            dishData.put("time", currentTime);

            dishResult.add(dishData);
        }
        result.put(_eating, dishResult);
        // Закрываем курсор
        cursor.close();

        Log.d("TEST", result.size() + "");

        controller.SetData(result);
    }
}

/*
    Выглядит так

    {
    ""ТЕКУЩЕЕ ВРЕМЯ ПРИЕМА ЕДЫ": [
            {
                "dish": "борщ",
                "mass": "300",
                "time": "20:10:23"
            },
            {
                "dish": "Чай",
                "mass": "350",
                "time": "01.12.59"
            }
        ]
    }
*/
