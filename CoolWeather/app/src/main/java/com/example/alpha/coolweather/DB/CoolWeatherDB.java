package com.example.alpha.coolweather.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ActionProvider;

import com.example.alpha.coolweather.Model.City;
import com.example.alpha.coolweather.Model.County;
import com.example.alpha.coolweather.Model.Province;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

/**
 * 数据库操作
 * Created by Alpha on 2016/7/19.
 */
public class CoolWeatherDB {
    public static final String DB_NAME="cool_weather";//数据库名
    public static final int VERSION=1;//数据库版本
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;
    private static final String Table_Province="Provinces";
    private static final String Table_City="Citys";
    private static final String Table_County="County";


    //获取数据库实体
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper helper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=helper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInsteance(Context context){
        if (coolWeatherDB==null){
            coolWeatherDB=new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    public void saveProvince(Province province){
        if (province!=null){
            ContentValues values=new ContentValues();
            values.put("name",province.getName());
            values.put("code",province.getCode());
            db.insert(Table_Province,null,values);
        }
    }

    //加载所有省份信息
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<>();
        Cursor cursor=db.query(Table_Province,null,null,null,null,null,null);
        if (cursor.moveToNext()){
            do {
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setName(cursor.getString(cursor.getColumnIndex("name")));
                province.setCode(cursor.getString(cursor.getColumnIndex("code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void saveCity(City city){
        if (city!=null){
            ContentValues values=new ContentValues();
            values.put("name",city.getName());
            values.put("code",city.getCode());
            values.put("province_id",city.getProvince_id());
            db.insert(Table_City,null, values);
        }
    }

    //根据省份id加载所有城市
    public List<City> loadCitys(int Provinceid){
        List<City> cities=new ArrayList<>();
        Cursor cursor=db.query(Table_City,null,"province_id=?",
                new String[]{String.valueOf(Provinceid)},null,null,null);
        if (cursor.moveToNext()){
            do {
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setName(cursor.getString(cursor.getColumnIndex("name")));
                city.setCode(cursor.getString(cursor.getColumnIndex("code")));
                city.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
                cities.add(city);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return cities;
    }

    public void saveCounty(County county){
        if (county!=null){
            ContentValues values=new ContentValues();
            values.put("name",county.getName());
            values.put("code",county.getCode());
            values.put("city_id",county.getCity_id());
            db.insert(Table_County,null,values);
        }
    }

    //根据城市id加载加载所有县
    public List<County> loadCouties(int Cityid){
        List<County> counties=new ArrayList<>();
        Cursor cursor=db.query(Table_County,null,"city_id=?",
                new String[]{String.valueOf(Cityid)},null,null,null);
        if (cursor.moveToNext()){
            do {
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setName(cursor.getString(cursor.getColumnIndex("name")));
                county.setCode(cursor.getString(cursor.getColumnIndex("code")));
                county.setCity_id(cursor.getInt(cursor.getColumnIndex("city_id")));
                counties.add(county);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return counties;
    }
}

