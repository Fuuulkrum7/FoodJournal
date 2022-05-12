package com.example.journal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

class FindUser extends Thread {
    String login;
    SQLiteDatabase db;
    private Cursor cursor;

    public FindUser(String login, SQLiteDatabase db) {
        this.db = db;
        this.login = login;
    }

    @Override
    public void run() {
        String[] projection = {
                DatabaseInfo.COLUMN_PASSWORD
        };

        // Формируем строку-выборку
        String selection = DatabaseInfo.COLUMN_LOGIN + " = '" + login + "'";

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
        } catch (Exception e) {
            // Если что-то пошло не так
            Log.d("TEST", e.toString());

            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось получить данные", Toast.LENGTH_SHORT);

            toast.show();
        }
    }

    public Cursor getCursor() {
        return cursor;
    }
}
