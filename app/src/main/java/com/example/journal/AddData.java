package com.example.journal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

class AddData extends Thread {
    protected ContentValues values;
    SQLiteDatabase db;
    String table;

    public AddData(ContentValues values, SQLiteDatabase db, String table) {
        this.values = values;
        this.db = db;
        this.table = table;
    }

    @Override
    public void run() {
        // Добавляем в бд
        try {
            db.insert(table, null, values);
        }
        // Если что-то пошло не так, то вот
        catch (Exception e) {
            Log.d("TEST", e.toString());
            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось добавить данные", Toast.LENGTH_SHORT);

            toast.show();
        }
    }
}
