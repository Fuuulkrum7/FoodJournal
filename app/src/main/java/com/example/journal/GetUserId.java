package com.example.journal;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

class GetUserId extends Thread {
    int id;
    SQLiteDatabase db;

    public GetUserId(SQLiteDatabase db) {
        this.db = db;
    }

    @SuppressLint("Recycle")
    public void run() {
        Cursor cursor;
        try {
            cursor = db.query(
                    DatabaseInfo.USER_TABLE,
                    new String[]{DatabaseInfo.COLUMN_USER_ID},
                    null,
                    null,
                    null,
                    null,
                    null
            );
        } catch (Exception e) {
            // Если что-то пошло не так
            Log.d(MainActivity.TAG, e.toString());

            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось отправить данные", Toast.LENGTH_SHORT);

            toast.show();

            return;
        }

        int idColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_USER_ID);
        cursor.moveToFirst();

        id = cursor.getInt(idColumnIndex);
    }

    public int getUId() {
        return id;
    }
}
