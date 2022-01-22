package com.example.journal;

import android.provider.BaseColumns;

// Просто данные по бд, какие тут ещё нужны пояснения
final class DatabaseInfo implements BaseColumns {
    public static final String TABLE_NAME = "journal";
    public static final String COLUMN_DISH = "dish";
    public static final String COLUMN_MASS = "mass";
    public static final String COLUMN_EATING = "eating";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TIME_ADD = "time_add";
    public static final String COLUMN_CALORIES = "calories";

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_DISH + " TEXT, " +
                    COLUMN_MASS + " INTEGER, " +
                    COLUMN_EATING + " INTEGER, " +
                    COLUMN_CALORIES + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_TIME_ADD + " TEXT)";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private DatabaseInfo() {}
}
