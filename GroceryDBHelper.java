
package com.example.grocerylistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroceryDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GroceryDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "grocery_items";

    public GroceryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "item_name TEXT, " +
                "purchased INTEGER DEFAULT 0)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addItem(String name) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("item_name", name);
        values.put("purchased", 0);

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    public Cursor getAllItems() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME,
                null
        );
    }

    public boolean updatePurchased(int id, int status) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("purchased", status);

        return db.update(
                TABLE_NAME,
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    public boolean deleteItem(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_NAME,
                "id=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }
}
