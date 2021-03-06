package com.example.alpha.coolweather.DB;

import android.content.Context;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 天气数据库类
 * Created by Alpha on 2016/7/19.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_PROVINCE="create table Provinces("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"code text)";
    private static final String CREATE_CITY="create table Citys("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"code text,"
            +"province_id integer)";
    private static final String CREATE_COUNTY="create table County("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"code text,"
            +"city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
