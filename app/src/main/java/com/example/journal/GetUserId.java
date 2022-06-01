package com.example.journal;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
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
            cursor =  db.rawQuery("select * from " + DatabaseInfo.USER_TABLE,null);;
        } catch (Exception e) {
            // Если что-то пошло не так
            Log.d(MainActivity.TAG, e.toString());

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.getContext(),
                        "Не удалось отправить данные", Toast.LENGTH_SHORT).show();
                }
            });


            return;
        }

        int idColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_USER_ID);
        cursor.moveToFirst();

        id = cursor.getInt(idColumnIndex);
        Log.d(MainActivity.TAG, id + "");
    }

    public int getUId() {
        return id;
    }
}
