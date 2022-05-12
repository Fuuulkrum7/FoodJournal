package com.example.journal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.NoSuchAlgorithmException;

class CheckUser extends Thread {
    SQLiteDatabase db;
    String login;
    String password;
    LoginFragment loginFragment;

    public CheckUser(SQLiteDatabase db, String login, String password, LoginFragment loginFragment) {
        this.db = db;
        this.login = login;
        this.password = password;
        this.loginFragment = loginFragment;
    }

    @Override
    public void run() {
        FindUser findUser = new FindUser(login, db);
        findUser.start();
        Cursor cursor;

        try {
            findUser.join();
            cursor = findUser.getCursor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        if (cursor == null) {
            return;
        }

        // Узнаем индекс каждого столбца
        int passwordColumnIndex = cursor.getColumnIndex(DatabaseInfo.COLUMN_PASSWORD);
        cursor.moveToNext();
        String new_password = "";
        if (cursor.getCount() != 0) {
            new_password = cursor.getString(passwordColumnIndex);
        }

        try {
            Log.d(MainActivity.TAG, password + " " + new_password);
            new_password = DatabaseInterface.getSSH512(new_password);

            loginFragment.onUserFound(new_password.equals(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            loginFragment.onUserFound(false);
        }
    }
}
