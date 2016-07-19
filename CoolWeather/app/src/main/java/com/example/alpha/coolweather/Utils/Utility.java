package com.example.alpha.coolweather.Utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.alpha.coolweather.DB.CoolWeatherDB;
import com.example.alpha.coolweather.Model.City;
import com.example.alpha.coolweather.Model.County;
import com.example.alpha.coolweather.Model.Province;

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
                    System.out.println(array[0]+"----------"+array[1]);
                    province.setName(array[1]);
                    coolWeatherDB.saveProvince(province);
                    Log.d("-----------------",p);
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
                    Log.d("-----------------",p);
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
                    Log.d("-----------------",p);
                }
                return true;
            }
        }
        return false;
    }
}
