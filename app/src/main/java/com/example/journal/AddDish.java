package com.example.journal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class AddDish extends Thread {
    protected ContentValues values;
    SQLiteDatabase db;
    DishFragment dishFragment;

    public AddDish(ContentValues values, SQLiteDatabase db, DishFragment dishFragment) {
        this.db = db;
        this.dishFragment = dishFragment;
        this.values = values;
    }

    @Override
    public void run() {
        AddData a = new AddData(values, db, DatabaseInfo.JOURNAL_TABLE);
        a.start();
        try {
            a.join();
            Cursor cursor = db.query(
                    DatabaseInfo.JOURNAL_TABLE,
                    new String[]{DatabaseInfo.COLUMN_ID},
                    DatabaseInfo.COLUMN_ID +
                            "= (SELECT MAX(" + DatabaseInfo.COLUMN_ID + ") FROM " +
                            DatabaseInfo.JOURNAL_TABLE + ")",
                    null,
                    null,
                    null,
                    DatabaseInfo.COLUMN_ID + " DESC",
                    "1"
            );

            int index = cursor.getColumnIndex(DatabaseInfo.COLUMN_ID);
            int id = -1;
            while (cursor.moveToNext()) {
                id = cursor.getInt(index);
            }

            if (dishFragment.id == -1)
                dishFragment.setId(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
