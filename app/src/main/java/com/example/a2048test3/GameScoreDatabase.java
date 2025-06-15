package com.example.a2048test3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GameScoreDatabase {

    private DatabaseHelper dbHelper;

    public GameScoreDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Zapisanie wyniku do bazy danych
    public void saveScore(String username, int moves) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Przygotowanie danych do wstawienia
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.COLUMN_MOVES, moves);

        // Wstawienie danych do bazy
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
        db.close();
    }

    // Pobranie wszystkich wyników
    public int getMoveCount(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int moveCount = 0;

        try {
            // Zapytanie do bazy w celu pobrania liczby ruchów
            cursor = db.query(DatabaseHelper.TABLE_NAME,
                    new String[]{DatabaseHelper.COLUMN_MOVES},
                    DatabaseHelper.COLUMN_USERNAME + " = ?",
                    new String[]{username},
                    null, null, null);

            // Sprawdzanie, czy kursor zawiera dane
            if (cursor != null && cursor.moveToFirst()) {
                moveCount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOVES));
            }
        } catch (Exception e) {
            // Logowanie błędu, jeśli wystąpi wyjątek
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();  // Zamykanie kursora
            }
            db.close();  // Zamykanie bazy danych
        }

        return moveCount;
    }

    public void updateMoveCount(String username, int moveCount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_MOVES, moveCount);

            // Aktualizacja liczby ruchów w bazie
            db.update(DatabaseHelper.TABLE_NAME, values,
                    DatabaseHelper.COLUMN_USERNAME + " = ?",
                    new String[]{username});
        } catch (Exception e) {
            // Logowanie błędu, jeśli wystąpi wyjątek
            e.printStackTrace();
        } finally {
            db.close();  // Zamykanie bazy danych
        }
    }
}
