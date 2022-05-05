package com.example.journal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

class UpdateDish extends Thread {
    int id;
    SQLiteDatabase db;
    ContentValues values;

    public UpdateDish(int id, SQLiteDatabase db, ContentValues values) {
        this.id = id;
        this.db = db;
        this.values = values;
    }

    @Override
    public void run() {
        db.update(
                DatabaseInfo.JOURNAL_TABLE,
                values,
                DatabaseInfo.COLUMN_ID + "=?",
                new String[]{Integer.toString(id)}
        );
        db.close();
    }
}
