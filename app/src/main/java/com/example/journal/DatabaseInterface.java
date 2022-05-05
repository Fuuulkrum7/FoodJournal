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

public class DatabaseInterface extends SQLiteOpenHelper {
    // Данные по бд
    public static final int DATABASE_VERSION = 4;
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

    public void addUser(ContentValues values){
        // Получаем бд для записи данных
        SQLiteDatabase db = this.getWritableDatabase();

        AddUser adder = new AddUser(values, db);
        adder.start();
    }

    // Соответсвтвенно, получем блюда
    public void getDishes(String date, EatingFragmentsController controller){
        SQLiteDatabase db = this.getReadableDatabase();
        GetDish getDish = new GetDish(date, db, controller);
        getDish.start();
    }

    public void checkUser(String login, String password, LoginFragment fragment){
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            password = getSSH512(password);
            CheckUser checkUser = new CheckUser(db, login, password, fragment);
            checkUser.start();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.getContext(), "Can't use db", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteData(int id, String table, String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        DeleteData deleteData = new DeleteData(id, table, key, db);
        deleteData.start();
    }

    public void updateData(int id, ContentValues values){
        SQLiteDatabase db = this.getReadableDatabase();
        UpdateDish updateDish = new UpdateDish(id, db, values);
        updateDish.start();
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

    protected static Cursor find_user(String login, SQLiteDatabase db) {
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
            return null;
        }

        return cursor;
    }
}


