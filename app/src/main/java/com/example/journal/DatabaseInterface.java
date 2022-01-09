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

import java.util.Date;
import java.text.SimpleDateFormat;

public class DatabaseInterface extends SQLiteOpenHelper {
    // Данные по бд
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LocalJournal.db";

    // Заготовка под дату. Не очень нравится, но это лучше, чем статичная переменная
    private String date = "";

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

    // Сеттер для даты
    public void setDate(String date){
        this.date = date;
    }

    // Добавляем блюдо в бд
    public void AddDish(String dish, int mass, int eating){
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

    // Соответсвтвенно, получем блюда
    public String GetDishes(int key){
        SQLiteDatabase db = this.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                        DatabaseInfo.COLUMN_DISH,
                        DatabaseInfo.COLUMN_MASS,
                        DatabaseInfo.COLUMN_EATING,
                        DatabaseInfo.COLUMN_DATE,
                        DatabaseInfo.COLUMN_TIME
        };

        // Формируем строку-выборку
        String selection = "";

        // Если нужна выборка по дате
        if (key == 1)
            selection = DatabaseInfo.COLUMN_DATE + " = '" + date + "'";
        // Если нужна выборка по времени приема пищи
        else if (key == 2)
            selection = DatabaseInfo.COLUMN_EATING;

        // Порядок сортировки. Сгачала сортируем по дате, а потом уже по еде
        String sortOrder =
                DatabaseInfo.COLUMN_DATE + ", " + DatabaseInfo.COLUMN_EATING + " ASC";

        Cursor cursor;
        try {
            // Делаем запрос
            cursor = db.query(
                    DatabaseInfo.TABLE_NAME,
                    projection,
                    key == 0 ? null : selection,
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

            return "";
        }

        // Узнаем индекс каждого столбца
        int dishColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_DISH);
        int massColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_MASS);
        int eatingColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_EATING);
        int dateColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_DATE);
        int timeColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_TIME);

        StringBuilder result = new StringBuilder();
        String _date = "";
        String _eating = "";
        String[] eating = MainActivity.getEating_values();

        // Пока в запросе ещё что-то есть
        while(cursor.moveToNext()) {
            // Получаем данные из запроса
            String currentDish = cursor.getString(dishColumnIndex);
            int currentMass = cursor.getInt(massColumnIndex);
            String currentEating = eating[cursor.getInt(eatingColumnIndex)];
            String currentDate = cursor.getString(dateColumnIndex);
            String currentTime = cursor.getString(timeColumnIndex);

            // Если данные начались за след. день
            if (! _date.equals(currentDate)){
                _date = currentDate;
                result.append("\n********************\n")
                        .append(currentDate)
                        .append("\n********************\n");
            }

            // Если данные за другой прием пищи
            if (! _eating.equals(currentEating)){
                _eating = currentEating;
                result.append("\n====================\n")
                        .append(currentEating)
                        .append("\n====================\n");
            }

            // Добаляем прочие данные
            result.append("Блюдо:").append(currentDish).append("\n")
                    .append("Масса:").append(currentMass).append("\n")
                    .append("Время добавления:").append(currentTime)
                    .append("\n\n");
        }

        // Закрываем курсор
        cursor.close();

        result.append("\n" + "********************\n");

        // Возвращаем строку. Использовать StringBuilder посоветовала IDE
        return result.toString();
    }
}