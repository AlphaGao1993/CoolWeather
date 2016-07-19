package com.example.alpha.coolweather.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.example.alpha.coolweather.Activity.WeatherActivity;
import com.example.alpha.coolweather.DB.CoolWeatherDB;
import com.example.alpha.coolweather.Model.City;
import com.example.alpha.coolweather.Model.County;
import com.example.alpha.coolweather.Model.Province;
import com.example.alpha.coolweather.Model.Weather;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * gson解析
 * Created by Alpha on 2016/7/19.
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,
                                                               String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if (allProvinces.length > 0){
                for (String p:allProvinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setCode(array[0]);
                    province.setName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
                                                               String response,int provinceid){
        if (!TextUtils.isEmpty(response)){
            String[] allCityies=response.split(",");
            if (allCityies.length > 0){
                for (String p:allCityies){
                    String[] array=p.split("\\|");
                    City city=new City();
                    city.setCode(array[0]);
                    city.setName(array[1]);
                    city.setProvince_id(provinceid);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                            String response,int cityid){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties=response.split(",");
            if (allCounties.length > 0){
                for (String p:allCounties){
                    String[] array=p.split("\\|");
                    County county=new County();
                    county.setCode(array[0]);
                    county.setName(array[1]);
                    county.setCity_id(cityid);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static void handWeatherResponse(Context context,String response){
        Gson gson=new Gson();
        Weather weatherinfo=gson.fromJson(response,Weather.class);
        SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sh.edit();
        if ("1002".equals(weatherinfo.getStatus())){
            Logger.d("该区域暂无数据");
            editor.putBoolean("updated_success",false);
        }else {
            String cityname=weatherinfo.getData().getCity();
            Weather.MForecast forecast=weatherinfo.getData().getForecast().get(0);
            String temp1=forecast.getLow().substring(2);
            String temp2=forecast.getHigh().substring(2);
            String weatherDesp=forecast.getType();
            String publishtime=forecast.getDate();
            editor.putBoolean("updated_success",true);
            /*JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherinfo=jsonObject.getJSONObject("weatherinfo");
            String cityname=weatherinfo.getString("city");
            String weathercode=weatherinfo.getString("cityid");
            String temp1=weatherinfo.getString("temp1");
            String temp2=weatherinfo.getString("temp2");
            String weatherDesp=weatherinfo.getString("weather");
            String publishtime=weatherinfo.getString("ptime");*/
            saveWeatherInfo(context,cityname,temp1,temp2,weatherDesp,publishtime);
        }
        editor.apply();
    }

    private static void saveWeatherInfo(Context context, String cityname, String temp1, String temp2, String weatherDesp, String publishtime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityname);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishtime);
        editor.putString("current_time",sdf.format(new Date()));
        editor.apply();
    }
}
