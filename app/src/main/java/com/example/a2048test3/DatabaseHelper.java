package com.example.a2048test3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game.db";  // Nazwa bazy danych
    private static final int DATABASE_VERSION = 2;         // Wersja bazy danych

    // Nazwa tabeli
    public static final String TABLE_NAME = "scores";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_MOVES = "moves";

    // SQL do tworzenia tabeli (dodanie kolumny 'moves')
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_MOVES + " INTEGER DEFAULT 0);";  // Ustawienie domyślnej liczby ruchów na 0

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Tworzenie bazy danych
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    // Aktualizacja bazy danych (dodanie kolumny moves w tabeli)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Sprawdzamy, czy kolumna 'moves' już istnieje
            Cursor cursor = db.rawQuery("PRAGMA table_info(" + DatabaseHelper.TABLE_NAME + ")", null);
            boolean columnExists = false;

            while (cursor.moveToNext()) {
                String columnName = cursor.getString(cursor.getColumnIndex("name"));
                if (columnName.equals(DatabaseHelper.COLUMN_MOVES)) {
                    columnExists = true;
                    break;
                }
            }
            cursor.close();

            // Jeśli kolumna 'moves' nie istnieje, dodajemy ją
            if (!columnExists) {
                db.execSQL("ALTER TABLE " + DatabaseHelper.TABLE_NAME + " ADD COLUMN " +
                        DatabaseHelper.COLUMN_MOVES + " INTEGER DEFAULT 0;");
            }
        }
    }
}
