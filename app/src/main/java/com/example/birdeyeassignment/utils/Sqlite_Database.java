package com.example.birdeyeassignment.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.birdeyeassignment.customers.model.All_Customers_Response;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Sqlite_Database extends SQLiteOpenHelper {
    Context mContext;
    private static final String LOG = "DatabaseHelper";
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "BirdEye";
    // TAble Name
    private static final String TABLE_CUSTOMERS = "customers";
    All_Customers_Response al;

    public static final String row_id = "row_id";
    public static final String number = "number";
    public static final String firstName = "firstName";
    public static final String middleName = "middleName";
    public static final String lastName = "lastName";
    public static final String emailId = "emailId";
    public static final String phone = "phone";


    private static final String CUSTOMERS = "CREATE TABLE "
            + TABLE_CUSTOMERS + "(" + row_id + " integer primary key autoincrement," +
            number + " TEXT," + firstName + " TEXT," + middleName + " TEXT," +
            phone + " TEXT," + lastName + " TEXT," + emailId + " TEXT" + ")";

    public Sqlite_Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;


    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(CUSTOMERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);

        onCreate(db);
    }


    public long insert_Customers(List<All_Customers_Response> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = 0;
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < list.size(); i++) {
                values.put(number, list.get(i).getNumber());
                values.put(firstName, list.get(i).getFirstName());
                values.put(middleName, list.get(i).getMiddleName());
                values.put(lastName, list.get(i).getLastName());
                values.put(emailId, list.get(i).getEmailId());
                values.put(phone, list.get(i).getPhone());
                id = db.insert(TABLE_CUSTOMERS, null, values);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();

        return id;
    }

    public int get_Customers_Count() {
        // TODO Auto-generated method stub
        ArrayList<HashMap<String, String>> occurlist = new ArrayList<HashMap<String, String>>();


        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_CUSTOMERS;
        long count = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            count = DatabaseUtils.queryNumEntries(db, TABLE_CUSTOMERS);
            db.close();
        }
        cursor.close();
        db.close();
        Log.i("occulist", occurlist + "");

        return (int) count;
    }

    public List<All_Customers_Response> get_All_Customers(int start_index, int end_index, String typed_search_text, String sort_type) {
        // TODO Auto-generated method stub
        List<All_Customers_Response> occurlist = new ArrayList<>();
        String selectQuery;
        if (typed_search_text.equalsIgnoreCase("")) {
            selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS + " ORDER BY " + firstName + " " + sort_type + " LIMIT "
                    + start_index + "," + end_index;
        } else {
            selectQuery = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + firstName + " LIKE " +
                    "'%" + typed_search_text + "%'" + " OR " + lastName + " LIKE " + "'%"
                    + typed_search_text + "%'" + " OR " + emailId + " LIKE " + "'%"
                    + typed_search_text + "%'" + " OR " + phone + " LIKE " + "'%"
                    + typed_search_text + "%'" + " ORDER BY " + firstName + " " + sort_type + " LIMIT "
                    + start_index + "," + end_index;


        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            do {

                All_Customers_Response res = new All_Customers_Response();
                res.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(number)));
                res.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(firstName)));
                res.setMiddleName(cursor.getString(cursor.getColumnIndexOrThrow(middleName)));
                res.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(lastName)));
                res.setEmailId(cursor.getString(cursor.getColumnIndexOrThrow(emailId)));
                res.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(phone)));

                occurlist.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.i("occulist", occurlist + "");

        return occurlist;
    }


    public void delete_Customer(String num) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (num.equals("")) {
            db.execSQL("DELETE FROM " + TABLE_CUSTOMERS);

        } else {
            db.execSQL("DELETE FROM " + TABLE_CUSTOMERS + " WHERE " + number + "='" + num + "'");
        }
        db.close();
    }


}
