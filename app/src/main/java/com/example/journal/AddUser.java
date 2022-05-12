package com.example.journal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

class AddUser extends Thread {
    protected ContentValues values;
    SQLiteDatabase db;
    String table;
    boolean success = false;

    public AddUser(ContentValues values, SQLiteDatabase db) {
        this.values = values;
        this.db = db;
        this.table = DatabaseInfo.USER_TABLE;
    }

    @Override
    public void run() {
        Cursor cursor;
        FindUser findUser = new FindUser(values.getAsString("login"), db);
        findUser.start();
        try {
            findUser.join();
            cursor = findUser.getCursor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Context context = MainActivity.getContext();
                    if (context == null) {
                        context = LoginFragment.getContext();
                    }

                    Toast.makeText(context, "Не удается получить данные", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        Context context = MainActivity.getContext();
        if (context == null) {
            context = LoginFragment.getContext();
        }
        if (cursor == null)
            return;

        else if (cursor.getCount() != 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Context context = MainActivity.getContext();
                    if (context == null) {
                        context = LoginFragment.getContext();
                    }

                    Toast.makeText(context, "Пользователь уже есть", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        db.execSQL("delete from " + table);

        // Добавляем в бд
        try {
            db.insert(table, null, values);
            success = true;
        }
        // Если что-то пошло не так, то вот
        catch (Exception e) {
            Log.d("TEST", e.toString());
            Toast toast = Toast.makeText(context,
                    "Не удалось добавить данные", Toast.LENGTH_SHORT);

            toast.show();
        }
    }
}
